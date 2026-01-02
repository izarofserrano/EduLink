package com.edulink.controller;


import com.edulink.dto.user.UserDTO;
import com.edulink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private final UserService userService;


    /**
     * Get user by ID (public basic info)
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.info("Request to get user with id: {}", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}



