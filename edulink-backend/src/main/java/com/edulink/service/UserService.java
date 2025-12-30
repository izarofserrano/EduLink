package com.edulink.service;


import com.edulink.exception.BusinessException;
import com.edulink.model.Student;
import com.edulink.model.User;
import com.edulink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {


    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;


    /**
     * Get public user information by ID
     */
    public Map<String, Object> getUserById(Long userId) {
        logger.info("Getting user info for userId: {}", userId);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("User not found with id: " + userId));


        logger.info("User found: {} (role: {})", user.getUsername(), user.getRole());


        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail() != null ? user.getEmail() : "");
        response.put("role", user.getRole() != null ? user.getRole().toString() : "UNKNOWN");
        response.put("createdAt", user.getCreatedAt());


        // Add reputation points for students
        if (user instanceof Student) {
            Student student = (Student) user;
            Double reputation = student.getReputationPoints();
            response.put("reputationPoints", reputation != null ? reputation : 0.0);
            logger.info("Student reputation points: {}", reputation);
        } else {
            response.put("reputationPoints", 0.0);
        }


        return response;
    }
}



