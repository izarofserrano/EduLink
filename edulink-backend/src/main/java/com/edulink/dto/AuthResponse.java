package com.edulink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;


/**
 * Authentication Response DTO
 */
@Builder
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String role;
    private String message;

    public AuthResponse(String token, String username, String email, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.message = "Authentication successful";
    }
}


