package com.fitness.tracker.controller;

import com.fitness.tracker.configuration.security.JwtUserDetailsService;
import com.fitness.tracker.dto.request.LoginRequest;
import com.fitness.tracker.dto.response.LoginResponse;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and token generation")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public AuthController(
        AuthenticationManager authenticationManager,
        JwtUtil jwtUtil,
        JwtUserDetailsService userDetailsService,
        UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Operation(
        summary = "Login to get JWT token",
        description = "Authenticate using email and password to receive a JWT token for subsequent API calls"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

            userDetailsService.loadUserByUsername(request.email());
            User user = userRepository.findByEmail(request.email()).orElseThrow();

            String token = jwtUtil.generateToken(user.getEmail(), List.of(user.getRole().name()));
            long expiresAt = System.currentTimeMillis() + jwtUtil.getExpirationTime();

            return ResponseEntity.ok(new LoginResponse(token, expiresAt, List.of(user.getRole().name())));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
