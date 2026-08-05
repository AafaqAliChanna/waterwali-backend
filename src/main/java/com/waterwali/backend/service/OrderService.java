package com.waterwali.backend.service;

import com.waterwali.backend.dto.CreateOrderRequest;
import com.waterwali.backend.dto.OrderResponse;
import com.waterwali.backend.entity.Order;
import com.waterwali.backend.entity.OrderStatus;
import com.waterwali.backend.entity.TankerSize;
import com.waterwali.backend.exception.ApiException;
import com.waterwali.backend.repository.OrderRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    // SRID 4326 = standard GPS coordinate system (WGS 84) -- this is what
    // Google Maps, GPS devices, and PostGIS all agree on. Always use it.
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    // Prices live ONLY here on the server. The app can never override them --
    // this prevents a tampered app from placing a PKR 1 order.
    private static final Map<TankerSize, BigDecimal> PRICES = new EnumMap<>(TankerSize.class);
    static {
        PRICES.put(TankerSize.SIZE_1000L, new BigDecimal("2000"));
        PRICES.put(TankerSize.SIZE_2000L, new BigDecimal("3500"));
        PRICES.put(TankerSize.SIZE_3000L, new BigDecimal("5000"));
        PRICES.put(TankerSize.SIZE_5000L, new BigDecimal("8000"));
    }

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(UUID customerId, CreateOrderRequest request) {
        BigDecimal price = PRICES.get(request.getTankerSize());
        if (price == null) {
            throw new ApiException("Unsupported tanker size", HttpStatus.BAD_REQUEST);
        }

        // JTS Point constructor takes (longitude, latitude) -- in that order, which
        // trips up almost every beginner, since we normally say "lat, long" out loud.
        Point pickupLocation = GEOMETRY_FACTORY.createPoint(
                new Coordinate(request.getLongitude(), request.getLatitude())
        );

        Order order = Order.builder()
                .customerId(customerId)
                .pickupLocation(pickupLocation)
                .tankerSize(request.getTankerSize())
                .price(price)
                .status(OrderStatus.PENDING)
                .build();

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    public List<OrderResponse> getMyOrders(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getOrder(UUID orderId, UUID requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        // A customer should only ever see their OWN orders -- never someone else's.
        if (!order.getCustomerId().equals(requesterId)) {
            throw new ApiException("You cannot view this order", HttpStatus.FORBIDDEN);
        }

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getDriverId(),
                order.getPickupLocation().getY(), // Y = latitude
                order.getPickupLocation().getX(), // X = longitude
                order.getTankerSize(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
