package com.waterwali.backend.repository;

import com.waterwali.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Spring Data JPA reads this interface name and auto-generates the SQL for you.
// You never write "SELECT * FROM users WHERE phone = ?" yourself.
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
}
