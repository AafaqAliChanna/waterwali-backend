package com.waterwali.backend.service;

import com.waterwali.backend.dto.AuthResponse;
import com.waterwali.backend.dto.LoginRequest;
import com.waterwali.backend.dto.RegisterRequest;
import com.waterwali.backend.entity.Role;
import com.waterwali.backend.entity.User;
import com.waterwali.backend.exception.ApiException;
import com.waterwali.backend.repository.UserRepository;
import com.waterwali.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ApiException("This phone number is already registered", HttpStatus.CONFLICT);
        }
        if (request.getRole() == Role.ADMIN) {
            throw new ApiException("Cannot self-register as admin", HttpStatus.FORBIDDEN);
        }

        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getPhone(), saved.getRole().name(), saved.getId().toString());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ApiException("Invalid phone or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Invalid phone or password", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getPhone(), user.getRole().name(), user.getId().toString());
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }
}
