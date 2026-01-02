package com.edulink.service;

import com.edulink.dto.user.*;
import com.edulink.exception.BusinessException;
import com.edulink.model.*;
import com.edulink.model.enums.UserRole;
import com.edulink.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get all users
     * @param adminUsername
     * @return
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(String adminUsername) {
        logger.info("Admin {} requesting all users", adminUsername);
        verifyAdminAccess(adminUsername);

        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId, String adminUsername) {
        logger.info("Admin {} requesting user {}", adminUsername, userId);
        verifyAdminAccess(adminUsername);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("User not found with id: " + userId));
        
        return convertToDTO(user);
    }

    /**
     * Create new user
     */
    public UserDTO createUser(UserCreateRequest request, String adminUsername) {
        logger.info("Admin {} creating user: {}", adminUsername, request.getUsername());
        verifyAdminAccess(adminUsername);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw BusinessException.conflict("Username already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw BusinessException.conflict("Email already registered: " + request.getEmail());
        }

        User user = createUserByRole(request);
        User savedUser = userRepository.save(user);

        logger.info("User created with id: {}", savedUser.getUserId());
        return convertToDTO(savedUser);
    }

    /**
     * Update user details
     */

    public UserDTO updateUser(Long userId, UserUpdateRequest request, String adminUsername) {
        logger.info("Admin {} updating user {}", adminUsername, userId);
        verifyAdminAccess(adminUsername);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("User not found with id: " + userId));

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getUserId().equals(userId)) {
                    throw BusinessException.conflict("Email already in use: " + request.getEmail());
                }
            });
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            userRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
                if (!existing.getUserId().equals(userId)) {
                    throw BusinessException.conflict("Username already in use: " + request.getUsername());
                }
            });
            user.setUsername(request.getUsername());
        }

        if (request.getRole() != null && user.getRole() != request.getRole()) {
            user = handleRoleChange(user, request.getRole());
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long userId, String adminUsername) {
        logger.info("Admin {} deleting user {}", adminUsername, userId);
        verifyAdminAccess(adminUsername);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("User not found with id: " + userId));

        if (user.getUsername().equals(adminUsername)) {
            throw BusinessException.forbidden("Cannot delete your own account");
        }

        if (user.getRole() == UserRole.ADMIN && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw BusinessException.forbidden("Cannot delete the last admin");
        }

        userRepository.delete(user);
        logger.info("User {} deleted", userId);
    }

    /**
     * Reset user password
     */

    public void resetPassword(Long userId, String newPassword, String adminUsername) {
        logger.info("Admin {} resetting password for user {}", adminUsername, userId);
        verifyAdminAccess(adminUsername);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Get system statistics
     */

    @Transactional(readOnly = true)
    public SystemStats getSystemStats(String adminUsername) {
        verifyAdminAccess(adminUsername);

        return SystemStats.builder()
                .totalUsers(userRepository.count())
                .totalStudents(userRepository.countByRole(UserRole.STUDENT))
                .totalTeachers(userRepository.countByRole(UserRole.TEACHER))
                .totalAdmins(userRepository.countByRole(UserRole.ADMIN))
                .totalDocuments(documentRepository.count())
                .totalApprovedDocuments(documentRepository.countByIsApproved(true))
                .totalPendingDocuments(documentRepository.countByIsApproved(false))
                .build();
    }

    /**
     * Search users by username or email
     */

    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(String query, String adminUsername) {
        verifyAdminAccess(adminUsername);

        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods

    private void verifyAdminAccess(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));

        if (user.getRole() != UserRole.ADMIN) {
            throw BusinessException.forbidden("Admin role required");
        }
    }

    // Create user entity based on role (if admin needs to create user)
    private User createUserByRole(UserCreateRequest request) {
        User user;
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        switch (request.getRole()) {
            case STUDENT:
                Student student = new Student();
                student.setReputationPoints(0.0);
                user = student;
                break;
            case TEACHER:
                user = new Teacher();
                break;
            case ADMIN:
                user = new Admin();
                break;
            default:
                throw new BusinessException("Invalid role: " + request.getRole());
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(request.getRole());
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    private User handleRoleChange(User user, UserRole newRole) {
        Long userId = user.getUserId();
        UserRole oldRole = user.getRole();

        // Delete old role entity
        switch (oldRole) {
            case STUDENT: studentRepository.deleteById(userId); break;
            case TEACHER: teacherRepository.deleteById(userId); break;
            case ADMIN: adminRepository.deleteById(userId); break;
        }

        // Create new role entity
        User newUser;
        switch (newRole) {
            case STUDENT:
                Student student = new Student();
                copyUserFields(user, student);
                student.setReputationPoints(0.0);
                newUser = studentRepository.save(student);
                break;
            case TEACHER:
                Teacher teacher = new Teacher();
                copyUserFields(user, teacher);
                newUser = teacherRepository.save(teacher);
                break;
            case ADMIN:
                Admin admin = new Admin();
                copyUserFields(user, admin);
                newUser = adminRepository.save(admin);
                break;
            default:
                throw new BusinessException("Invalid role: " + newRole);
        }

        return newUser;
    }

    private void copyUserFields(User source, User target) {
        target.setUserId(source.getUserId());
        target.setUsername(source.getUsername());
        target.setEmail(source.getEmail());
        target.setPassword(source.getPassword());
        target.setRole(source.getRole());
        target.setCreatedAt(source.getCreatedAt());
    }

    private UserDTO convertToDTO(User user) {
        Long documentCount = documentRepository.countByUploaderUserId(user.getUserId());

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .createdAt(user.getCreatedAt())
                .reputationPoints(user instanceof Student ? ((Student) user).getReputationPoints() : null)
                .documentsUploaded(documentCount.intValue())
                .status("active")
                .build();
    }
}


