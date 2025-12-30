package com.edulink.service;


import com.edulink.dto.course.CourseDTO;
import com.edulink.exception.BusinessException;
import com.edulink.model.Course;
import com.edulink.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {


    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);


    private final CourseRepository courseRepository;


    /**
     * Get all courses
     */
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Get course by ID
     */
    public CourseDTO getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Course not found with id: " + courseId));
        
        return convertToDTO(course);
    }


    // ==================== HELPER METHODS ====================


    private CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .courseId(course.getCourseId())
                .code(course.getCode())
                .courseName(course.getCourseName())
                .teacherName(course.getTeacherName())
                .semester(course.getSemester())
                .build();
    }
}



