package com.waterwali.backend.dto;

import com.waterwali.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String name;
    private Role role;
}
