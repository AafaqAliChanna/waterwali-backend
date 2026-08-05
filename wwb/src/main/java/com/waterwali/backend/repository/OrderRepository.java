package com.waterwali.backend.repository;

import com.waterwali.backend.entity.Order;
import com.waterwali.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByDriverId(UUID driverId);
}
