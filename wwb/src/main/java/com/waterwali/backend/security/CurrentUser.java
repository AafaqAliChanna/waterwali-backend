package com.waterwali.backend.security;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public class CurrentUser {
    public static UUID id(Authentication authentication) {
        return UUID.fromString((String) authentication.getDetails());
    }
}
