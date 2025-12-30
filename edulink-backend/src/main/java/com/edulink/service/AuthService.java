package com.edulink.service;

import com.edulink.dto.AuthResponse;
import com.edulink.dto.LoginRequest;
import com.edulink.dto.RegisterRequest;
import com.edulink.model.*;
import com.edulink.model.enums.UserRole;
import com.edulink.repository.UserRepository;
import com.edulink.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create user based on role
        User user = createUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save user
        user = userRepository.save(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), 
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private User createUser(RegisterRequest request) {
        return switch (request.getRole()) {
            case STUDENT -> {
                Student student = new Student();
                student.setReputationPoints(0.0);
                yield student;
            }
            case TEACHER -> new Teacher();
            case ADMIN -> new Admin();
            default -> throw new RuntimeException("Invalid role");
        };
    }
}