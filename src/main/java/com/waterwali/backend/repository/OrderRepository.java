package com.waterwali.backend.repository;

import com.waterwali.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    // Phase 3 will add a @Query here using ST_DWithin to find nearby PENDING orders for drivers.
}
