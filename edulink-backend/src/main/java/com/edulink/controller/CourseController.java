package com.edulink.controller;


import com.edulink.dto.course.CourseDTO;
import com.edulink.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CourseController {


    private final CourseService courseService;


    /**
     * Get all courses
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        List<CourseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }


    /**
     * Get course by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
}
