package com.edulink.service;

import com.edulink.model.*;
import com.edulink.repository.*;
import com.edulink.dto.teacher.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentRatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumThreadRepository forumThreadRepository;

    /**
     * Get all courses taught by a teacher
     */
    public List<TeacherCourseDTO> getTeacherCourses(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().name().equals("TEACHER")) {
            throw new RuntimeException("User is not a teacher");
        }

        List<Course> courses = courseRepository.findByTeacherName(username);

        return courses.stream()
                .map(course -> convertToCourseDTO(course))
                .collect(Collectors.toList());
    }

    /**
     * Get detailed statistics for a specific course
     */
    public CourseStatisticsDTO getCourseStatistics(Long courseId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacherName().equals(username)) {
            throw new RuntimeException("Access denied");
        }

        CourseStatisticsDTO stats = new CourseStatisticsDTO();
        stats.setCourseId(courseId);
        stats.setCourseName(course.getCourseName());
        stats.setCourseCode(course.getCode());

        List<Document> documents = documentRepository.findByCourse_CourseId(courseId);

        stats.setTotalDocuments(documents.size());
        stats.setOfficialDocuments((int) documents.stream()
                .filter(doc -> doc.getUploader() != null && 
                       doc.getUploader().getRole().name().equals("TEACHER"))
                .count());
        stats.setStudentDocuments(documents.size() - stats.getOfficialDocuments());
        
        int totalDownloads = documents.stream()
                .mapToInt(Document::getDownloadCount)
                .sum();
        stats.setTotalDownloads(totalDownloads);

        //calculate average rating
        double avgRating = documents.stream()
                .mapToDouble(doc -> {
                    Double rating = ratingRepository.getAverageRatingForDocument(doc.getDocumentId());
                    return rating != null ? rating : 0.0;
                })
                .average()
                .orElse(0.0);
        stats.setAverageRating(avgRating);

        //chose top 5 documents by downloads
        List<DocumentStatsDTO> topDocuments = documents.stream()
                .sorted(Comparator.comparingInt(Document::getDownloadCount).reversed())
                .limit(5)
                .map(this::convertToDocumentStatsDTO)
                .collect(Collectors.toList());
        stats.setTopDocuments(topDocuments);

        long unansweredQuestions = forumThreadRepository.countByCourseAndUnanswered(courseId);
        stats.setUnansweredQuestions((int) unansweredQuestions);

        return stats;
    }

    // Helper Methods
    private TeacherCourseDTO convertToCourseDTO(Course course) {
        TeacherCourseDTO dto = new TeacherCourseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCode(course.getCode());
        dto.setCourseName(course.getCourseName());
        dto.setSemester(course.getSemester());

        int docCount = documentRepository.findByCourse_CourseId(course.getCourseId()).size();
        dto.setDocumentCount(docCount);

        List<Document> docs = documentRepository.findByCourse_CourseId(course.getCourseId());
        long studentCount = docs.stream()
                .map(doc -> doc.getUploader())
                .filter(Objects::nonNull)
                .filter(user -> user.getRole().name().equals("STUDENT"))
                .map(User::getUserId)
                .distinct()
                .count();
        dto.setStudentCount((int) studentCount);

        return dto;
    }

    private DocumentStatsDTO convertToDocumentStatsDTO(Document doc) {
        DocumentStatsDTO dto = new DocumentStatsDTO();
        dto.setDocumentId(doc.getDocumentId());
        dto.setTitle(doc.getDocTitle());
        dto.setDownloads(doc.getDownloadCount());
        
        Double rating = ratingRepository.getAverageRatingForDocument(doc.getDocumentId());
        dto.setRating(rating != null ? rating : 0.0);
        
        return dto;
    }


    
    

    
}