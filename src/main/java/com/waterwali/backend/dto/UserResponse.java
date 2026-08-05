package com.waterwali.backend.dto;

import com.waterwali.backend.entity.Role;
import com.waterwali.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

// Used by GET /api/users/me. NEVER return the User entity directly from a
// controller -- it has passwordHash on it, and that would leak the hash
// (even hashed, it should never leave the server) straight into the JSON response.
@Data
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String phone;
    private Role role;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getPhone(), user.getRole());
    }
}
