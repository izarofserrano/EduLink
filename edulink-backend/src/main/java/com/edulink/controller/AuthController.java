package com.edulink.controller;


import com.edulink.dto.AuthResponse;
import com.edulink.dto.LoginRequest;
import com.edulink.dto.RegisterRequest;
import com.edulink.dto.user.UserDTO;
import com.edulink.exception.BusinessException;
import com.edulink.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


/**
 * Authentication Controller
 * Handles user registration, login, and token validation
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class AuthController {


    private final AuthService authService;


    /**
     * Register a new user
     * @param request Registration details
     * @return AuthResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Authenticate user and generate token
     * @param request Login credentials
     * @return AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        log.info("User logged in successfully: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }


    /**
     * Get current authenticated user details
     * @param userDetails Authenticated user from security context
     * @return Current user information
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new BusinessException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
        
        log.debug("Fetching current user: {}", userDetails.getUsername());
        UserDTO user = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }


    /**
     * Validate JWT token
     * @param userDetails Authenticated user from security context
     * @return Token validation result
     */
    @GetMapping("/validate")
    public ResponseEntity<ValidationResponse> validateToken(@AuthenticationPrincipal UserDetails userDetails) {
        boolean isValid = userDetails != null && !"anonymousUser".equals(userDetails.getUsername());
        
        ValidationResponse response = ValidationResponse.builder()
                .valid(isValid)
                .username(isValid ? userDetails.getUsername() : null)
                .build();
        
        return ResponseEntity.ok(response);
    }


    /**
     * Logout user (client-side token removal)
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        log.info("User logout");
        return ResponseEntity.ok(new MessageResponse("Logout successful"));
    }


    // ===========================
    // Response DTOs
    // ===========================


    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValidationResponse {
        private boolean valid;
        private String username;
    }


    @lombok.Data
    @lombok.AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }
}



