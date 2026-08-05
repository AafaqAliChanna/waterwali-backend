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

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(orderService.createOrder(CurrentUser.id(authentication), request));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(CurrentUser.id(authentication)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrder(id, CurrentUser.id(authentication)));
    }
}
