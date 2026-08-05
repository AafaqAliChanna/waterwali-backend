package com.waterwali.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// This IS the "orders" table. pickupLocation is a PostGIS "Point" --
// a real geographic type, not just two plain numbers -- which is what lets
// Phase 3's "find nearest driver" query work efficiently.
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID customerId;

    // Nullable because no driver is assigned yet when the order is first created (Phase 3 fills this in).
    private UUID driverId;

    @Column(columnDefinition = "geography(Point,4326)", nullable = false)
    private Point pickupLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TankerSize tankerSize;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }
}
