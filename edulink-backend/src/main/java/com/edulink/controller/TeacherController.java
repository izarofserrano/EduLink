package com.edulink.controller;

import com.edulink.dto.teacher.CourseStatisticsDTO;
import com.edulink.dto.teacher.TeacherCourseDTO;
import com.edulink.service.TeacherService;
import com.edulink.service.TeacherService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/courses")
    public ResponseEntity<?> getMyCourses() {
        try {
            String username = getAuthenticatedUsername();
            
            List<TeacherCourseDTO> courses = teacherService.getTeacherCourses(username);
            
            return ResponseEntity.ok(courses);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/courses/{courseId}/statistics")
    public ResponseEntity<?> getCourseStatistics(@PathVariable Long courseId) {
        try {
            String username = getAuthenticatedUsername();
            
            CourseStatisticsDTO stats = teacherService.getCourseStatistics(courseId, username);
            
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }
        return authentication.getName();
    }
}