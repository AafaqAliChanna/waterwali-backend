package com.waterwali.backend.controller;

import com.waterwali.backend.dto.UserResponse;
import com.waterwali.backend.entity.User;
import com.waterwali.backend.exception.ApiException;
import com.waterwali.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET http://localhost:8080/api/users/me  (requires "Authorization: Bearer <token>")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName(); // The JWT token stores the user's phone number

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        // FIXED: previously returned the raw User entity, which includes passwordHash.
        // Even a hashed password should never leave the server in an API response.
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
