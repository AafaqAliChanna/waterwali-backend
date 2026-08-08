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

// This is "the brain" for authentication. Controllers stay thin;
// all the actual decisions happen here.
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    // Drivers need a wallet immediately so balance checks work from their first login.
    private final WalletService walletService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.walletService = walletService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ApiException("This phone number is already registered", HttpStatus.CONFLICT);
        }

        // A client can never register themselves as ADMIN -- safety check on the server,
        // never trust what the app claims to be.
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

        // Create the driver's wallet at registration so online access can be checked safely.
        if (saved.getRole() == Role.DRIVER) {
            walletService.createWalletForDriver(saved.getId());
        }

        String token = jwtUtil.generateToken(saved.getPhone(), saved.getRole().name(), saved.getId().toString());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new ApiException("Invalid phone or password", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            // Deliberately same error message as "user not found" above --
            // never reveal WHICH part was wrong, that helps attackers guess valid phone numbers.
            throw new ApiException("Invalid phone or password", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getPhone(), user.getRole().name(), user.getId().toString());
        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }
}
