package com.edulink.repository;

import com.edulink.model.User;
import com.edulink.model.enums.UserRole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByRole(UserRole admin);
    List<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String query, String query2);
}
