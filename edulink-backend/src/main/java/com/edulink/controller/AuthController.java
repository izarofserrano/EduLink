package com.edulink.controller;

import com.edulink.dto.AuthResponse;
import com.edulink.dto.LoginRequest;
import com.edulink.dto.RegisterRequest;
import com.edulink.model.User;
import com.edulink.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = authService.getCurrentUser(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
                
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", authentication.getName()
                ));
            }
            
            return ResponseEntity.ok(Map.of("valid", false));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}