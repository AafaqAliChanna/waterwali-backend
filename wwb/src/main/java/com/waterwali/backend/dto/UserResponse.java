package com.waterwali.backend.dto;

import com.waterwali.backend.entity.Role;
import com.waterwali.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

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
