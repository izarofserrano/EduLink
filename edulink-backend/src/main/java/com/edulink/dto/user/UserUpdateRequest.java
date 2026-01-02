package com.edulink.dto.user;

import com.edulink.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/***
 * User Update Request DTO
 */

@Data
public class UserUpdateRequest {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private UserRole role;
}


