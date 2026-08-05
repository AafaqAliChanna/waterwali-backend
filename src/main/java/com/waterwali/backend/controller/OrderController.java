package com.waterwali.backend.controller;

import com.waterwali.backend.dto.CreateOrderRequest;
import com.waterwali.backend.dto.OrderResponse;
import com.waterwali.backend.security.CurrentUser;
import com.waterwali.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // POST http://localhost:8080/api/orders   (requires "Authorization: Bearer <token>")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                                      Authentication authentication) {
        UUID customerId = CurrentUser.id(authentication);
        return ResponseEntity.ok(orderService.createOrder(customerId, request));
    }

    // GET http://localhost:8080/api/orders/mine
    @GetMapping("/mine")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        UUID customerId = CurrentUser.id(authentication);
        return ResponseEntity.ok(orderService.getMyOrders(customerId));
    }

    // GET http://localhost:8080/api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id, Authentication authentication) {
        UUID requesterId = CurrentUser.id(authentication);
        return ResponseEntity.ok(orderService.getOrder(id, requesterId));
    }
}
