package com.edulink.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private Double reputationPoints;
    private Integer documentsUploaded;
    private String status; // active, suspended, etc
}

