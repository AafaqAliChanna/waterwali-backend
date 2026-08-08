package com.waterwali.backend.controller;

import com.waterwali.backend.entity.User;
import com.waterwali.backend.exception.ApiException;
import com.waterwali.backend.repository.UserRepository;
import com.waterwali.backend.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    private final UserRepository userRepository;
    // WalletService enforces the minimum balance before a driver can go online.
    private final WalletService walletService;

    public DriverController(UserRepository userRepository, WalletService walletService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @PostMapping("/online")
    public ResponseEntity<Map<String, Boolean>> goOnline(Authentication authentication) {
        return setOnlineStatus(authentication, true);
    }

    @PostMapping("/offline")
    public ResponseEntity<Map<String, Boolean>> goOffline(Authentication authentication) {
        return setOnlineStatus(authentication, false);
    }

    private ResponseEntity<Map<String, Boolean>> setOnlineStatus(Authentication authentication, boolean online) {
        String phone = authentication.getName();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (user.getRole() != com.waterwali.backend.entity.Role.DRIVER) {
            throw new ApiException("Only drivers can go online/offline", HttpStatus.FORBIDDEN);
        }

        // Drivers must keep the required wallet balance before accepting new work.
        if (online) {
            walletService.assertCanGoOnline(user.getId());
        }
        user.setOnline(online);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("isOnline", online));
    }
}