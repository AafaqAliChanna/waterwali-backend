package com.waterwali.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// This filter runs BEFORE every controller. It checks the "Authorization" header
// for a valid JWT. If valid, it tells Spring Security "this request is from
// this user, with this role" so @PreAuthorize checks work downstream.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.extractClaims(token);
                String phone = claims.getSubject();
                String role = claims.get("role", String.class);

                String userId = claims.get("userId", String.class);

                var authToken = new UsernamePasswordAuthenticationToken(
                        phone,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                // Controllers read this back via CurrentUser.id(authentication) to know
                // WHICH user is making the request -- never trust a userId sent in the request body.
                authToken.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
