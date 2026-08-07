
package com.waterwali.backend.repository;

import com.waterwali.backend.entity.Order;
import com.waterwali.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByDriverId(UUID driverId);

    // ST_DWithin = PostGIS's "is this point within X meters of that point?" check.
    // Much faster than pulling every order into Java and calculating distance by hand.
    @Query(value = """
            SELECT * FROM orders o
            WHERE o.status = 'PENDING'
              AND ST_DWithin(o.pickup_location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326), :radiusMeters)
            ORDER BY ST_Distance(o.pickup_location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)) ASC
            """, nativeQuery = true)
    List<Order> findNearbyPending(@Param("lat") double lat, @Param("lng") double lng, @Param("radiusMeters") double radiusMeters);

    // The "only one driver wins" trick: this UPDATE only succeeds if the order
    // is STILL 'PENDING' at the exact moment it runs. If a second driver's request
    // arrives a millisecond later, status is no longer 'PENDING', so 0 rows update --
    // that's how we know they lost the race, without any manual locking code.
    @Modifying
    @Query("UPDATE Order o SET o.driverId = :driverId, o.status = 'ACCEPTED' WHERE o.id = :orderId AND o.status = 'PENDING'")
    int acceptOrder(@Param("orderId") UUID orderId, @Param("driverId") UUID driverId);
}