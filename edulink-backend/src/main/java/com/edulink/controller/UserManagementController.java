package com.edulink.controller;

import com.edulink.dto.user.*;
import com.edulink.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);
    private final UserManagementService userManagementService;

    /**
     * Get all users (admin only)
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} requesting all users", userDetails.getUsername());
        List<UserDTO> users = userManagementService.getAllUsers(userDetails.getUsername());
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID (admin only)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} requesting user details for userId: {}", userDetails.getUsername(), id);
        UserDTO user = userManagementService.getUserById(id, userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * Create new user (admin only)
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody UserCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} creating new user", userDetails.getUsername());
        UserDTO createdUser = userManagementService.createUser(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Update user information (admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} updating user {}", userDetails.getUsername(), id);
        UserDTO updatedUser = userManagementService.updateUser(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} deleting user {}", userDetails.getUsername(), id);
        userManagementService.deleteUser(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    /**
     * Reset user password (admin only)
     */
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordResetRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} resetting password for user {}", userDetails.getUsername(), id);
        userManagementService.resetPassword(id, request.getNewPassword(), userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    /**
     * Get system statistics (admin only)
     */
    @GetMapping("/stats")
    public ResponseEntity<SystemStats> getSystemStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} requesting system stats", userDetails.getUsername());
        SystemStats stats = userManagementService.getSystemStats(userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }

    /**
     * Search users (admin only)
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} searching users with query: {}", userDetails.getUsername(), query);
        List<UserDTO> users = userManagementService.searchUsers(query, userDetails.getUsername());
        return ResponseEntity.ok(users);
    }
}


