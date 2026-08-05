package com.waterwali.backend.dto;

import com.waterwali.backend.entity.OrderStatus;
import com.waterwali.backend.entity.TankerSize;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// What the app actually receives back -- plain lat/lng, never the raw PostGIS Point object.
@Data
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID customerId;
    private UUID driverId;
    private Double latitude;
    private Double longitude;
    private TankerSize tankerSize;
    private BigDecimal price;
    private OrderStatus status;
    private Instant createdAt;
}
