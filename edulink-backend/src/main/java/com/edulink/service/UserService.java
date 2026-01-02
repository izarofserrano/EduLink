package com.edulink.service;


import com.edulink.dto.user.UserDTO;
import com.edulink.exception.BusinessException;
import com.edulink.model.Student;
import com.edulink.model.User;
import com.edulink.repository.DocumentRepository;
import com.edulink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * User Service
 * Handles user-related business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;


    /**
     * Get public user information by ID
     * @param userId User ID
     * @return Public user information
     * @throws BusinessException if user not found
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        log.debug("Fetching user info for userId: {}", userId);


        User user = findUserById(userId);
        
        log.debug("User found: {} (role: {})", user.getUsername(), user.getRole());


        return convertToUserDTO(user);
    }


    // ===========================
    // Private Helper Methods
    // ===========================


    /**
     * Find user by ID
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        "User not found with id: " + userId,
                        HttpStatus.NOT_FOUND
                ));
    }


    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO.UserDTOBuilder builder = UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : "UNKNOWN")
                .createdAt(user.getCreatedAt())
                .status("active"); // Default status


        // Add student-specific data
        if (user instanceof Student student) {
            Double reputation = student.getReputationPoints();
            builder.reputationPoints(reputation != null ? reputation : 0.0);
            
            // Count documents uploaded by student
            long documentCount = documentRepository.countByUploaderUserId(user.getUserId());
            builder.documentsUploaded((int) documentCount);
            
            log.debug("Student {} has {} reputation points and {} documents",
                    user.getUsername(), reputation, documentCount);
        } else {
            // Non-students don't have reputation
            builder.reputationPoints(null); // Will be excluded from JSON due to @JsonInclude
        }


        return builder.build();
    }
}



