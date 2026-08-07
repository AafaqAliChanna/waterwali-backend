package com.waterwali.backend.security;

import org.springframework.security.core.Authentication;

import java.util.UUID;

// A tiny helper so every controller doesn't repeat the same casting logic.
// Always get the user's identity from the verified JWT (via this class) --
// NEVER from a userId field the client sent in the request body, since that
// could be tampered with to impersonate someone else.
public class CurrentUser {

    public static boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                 .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
    public static UUID id(Authentication authentication) {
        return UUID.fromString((String) authentication.getDetails());
    }
}
