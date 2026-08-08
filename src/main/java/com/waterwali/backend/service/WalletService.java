package com.waterwali.backend.service;

import com.waterwali.backend.dto.OrderResponse;
import com.waterwali.backend.dto.WalletResponse;
import com.waterwali.backend.entity.*;
import com.waterwali.backend.exception.ApiException;
import com.waterwali.backend.repository.OrderRepository;
import com.waterwali.backend.repository.UserRepository;
import com.waterwali.backend.repository.WalletLedgerRepository;
import com.waterwali.backend.repository.WalletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class WalletService {

    // The ONLY commission rate, in one place. If it ever changes, change it here only.
    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.07");
    // Below this balance, a driver is blocked from going online again.
    private static final BigDecimal MIN_BALANCE = new BigDecimal("200");

    private final WalletRepository walletRepository;
    private final WalletLedgerRepository ledgerRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, WalletLedgerRepository ledgerRepository,
                          OrderRepository orderRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.ledgerRepository = ledgerRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // Called once, when a DRIVER registers (see AuthService). Every driver
    // must have exactly one wallet, starting at 0.
    public Wallet createWalletForDriver(UUID driverId) {
        Wallet wallet = Wallet.builder()
                .driverId(driverId)
                .balance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(wallet);
    }

    public Wallet getWallet(UUID driverId) {
        return walletRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ApiException("Wallet not found for this driver", HttpStatus.NOT_FOUND));
    }

    public WalletResponse getWalletResponse(UUID driverId) {
        Wallet wallet = getWallet(driverId);
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ApiException("Driver not found", HttpStatus.NOT_FOUND));
        return new WalletResponse(wallet.getBalance(), driver.isOnline());
    }

    // THE MOST IMPORTANT METHOD IN THE WHOLE APP.
    // @Transactional means: every write below happens together, or NONE of them
    // do. If the server crashes after step 2 but before step 4, the database
    // automatically undoes steps 1-2 as if nothing happened. No half-deducted
    // wallets, ever.
    @Transactional
    public OrderResponse completeOrder(UUID orderId, UUID driverId) {
        // Step 1: find the order and make sure THIS driver is allowed to complete it.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        if (!driverId.equals(order.getDriverId())) {
            throw new ApiException("You are not assigned to this order", HttpStatus.FORBIDDEN);
        }
        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new ApiException("Order cannot be completed from its current status", HttpStatus.CONFLICT);
        }

        // Step 2: mark the order as done.
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // Step 3: calculate the commission. setScale(2, HALF_UP) rounds cleanly
        // to 2 decimal places (e.g. PKR 280.00), the way real money must always be handled.
        BigDecimal commission = order.getPrice()
                .multiply(COMMISSION_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        // Step 4: write the permanent receipt FIRST.
        Wallet wallet = getWallet(driverId);
        WalletLedger ledgerEntry = WalletLedger.builder()
                .walletId(wallet.getId())
                .orderId(order.getId())
                .amount(commission.negate()) // negative = money leaving
                .type(LedgerType.COMMISSION)
                .build();
        ledgerRepository.save(ledgerEntry);

        // Step 5: update the quick-read balance to match.
        wallet.setBalance(wallet.getBalance().subtract(commission));
        walletRepository.save(wallet);

        // Step 6: if balance dropped too low, block the driver from going online again.
        if (wallet.getBalance().compareTo(MIN_BALANCE) < 0) {
            User driver = userRepository.findById(driverId)
                    .orElseThrow(() -> new ApiException("Driver not found", HttpStatus.NOT_FOUND));
            driver.setOnline(false);
            userRepository.save(driver);
        }

        return new OrderResponse(
                order.getId(), order.getCustomerId(), order.getDriverId(),
                order.getPickupLocation().getY(), order.getPickupLocation().getX(),
                order.getTankerSize(), order.getPrice(), order.getStatus(), order.getCreatedAt()
        );
    }

    // MVP-only manual top-up (self-service). In a real launch this should be
    // admin-only, or triggered by a real payment (JazzCash/Easypaisa), never
    // a driver just typing in any amount they want.
    @Transactional
    public WalletResponse topUp(UUID driverId, BigDecimal amount) {
        Wallet wallet = getWallet(driverId);

        WalletLedger ledgerEntry = WalletLedger.builder()
                .walletId(wallet.getId())
                .orderId(null)
                .amount(amount)
                .type(LedgerType.TOPUP)
                .build();
        ledgerRepository.save(ledgerEntry);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new ApiException("Driver not found", HttpStatus.NOT_FOUND));
        return new WalletResponse(wallet.getBalance(), driver.isOnline());
    }

    // Used by DriverController before allowing "go online".
    public void assertCanGoOnline(UUID driverId) {
        Wallet wallet = getWallet(driverId);
        if (wallet.getBalance().compareTo(MIN_BALANCE) < 0) {
            throw new ApiException(
                    "Wallet balance too low (minimum PKR " + MIN_BALANCE + "). Please top up first.",
                    HttpStatus.FORBIDDEN
            );
        }
    }
}