package com.waterwali.backend.dto;

import com.waterwali.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

// This is the "ID card" the app receives and stores after login.
// Flutter attaches the token to every future request's Authorization header.
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String name;
    private Role role;
}
