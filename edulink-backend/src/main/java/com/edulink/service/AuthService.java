package com.edulink.service;


import com.edulink.dto.AuthResponse;
import com.edulink.dto.LoginRequest;
import com.edulink.dto.RegisterRequest;
import com.edulink.dto.user.UserDTO;
import com.edulink.exception.BusinessException;
import com.edulink.model.*;
import com.edulink.model.enums.UserRole;
import com.edulink.repository.UserRepository;
import com.edulink.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;


/**
 * Authentication Service
 * Handles user registration, login, and user management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    /**
     * Register a new user
     * @param request Registration details
     * @return AuthResponse with JWT token
     * @throws BusinessException if username or email already exists
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new user: {}", request.getUsername());


        // Validate username if it is unique
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException("Username already exists", HttpStatus.CONFLICT);
        }


        // Validate email if it is unique
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email already exists", HttpStatus.CONFLICT);
        }

        // Create user entity based on role
        User user = createUserByRole(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        // Save user
        user = userRepository.save(user);
        log.info("User created successfully: {} with role: {}", user.getUsername(), user.getRole());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return buildAuthResponse(user, token, "Registration successful");
    }


    /**
     * Authenticate user and generate token
     * @param request Login credentials
     * @return AuthResponse with JWT token
     * @throws BusinessException if authentication fails
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.debug("Attempting login for user: {}", request.getUsername());


        try {
            // Authenticate credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );


            // Fetch user details
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));


            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());


            log.info("User authenticated successfully: {}", user.getUsername());
            return buildAuthResponse(user, token, "Login successful");


        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for user: {}", request.getUsername());
            throw new BusinessException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            log.error("Authentication error for user: {}", request.getUsername(), e);
            throw new BusinessException("Authentication failed", HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * Get current authenticated user details
     * @param username Username from JWT token
     * @return UserDTO with user details
     * @throws BusinessException if user not found
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String username) {
        log.debug("Fetching user details for: {}", username);


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));


        return convertToUserDTO(user);
    }


    // Helper methods


    /**
     * Create user entity based on role
     */
    private User createUserByRole(RegisterRequest request) {
        User user = switch (request.getRole()) {
            case STUDENT -> {
                Student student = new Student();
                student.setReputationPoints(0.0);
                yield student;
            }
            case TEACHER -> new Teacher();
            case ADMIN -> new Admin();
            default -> throw new BusinessException("Invalid role: " + request.getRole(), HttpStatus.BAD_REQUEST);
        };


        // Set common fields
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());


        return user;
    }


    /**
     * Build AuthResponse DTO
     */
    private AuthResponse buildAuthResponse(User user, String token, String message) {
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message(message)
                .build();
    }


    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO.UserDTOBuilder builder = UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .status("active");


        // Add role-specific data
        if (user instanceof Student student) {
            builder.reputationPoints(student.getReputationPoints());
            // Could add documentsUploaded count here
        }


        return builder.build();
    }
}



