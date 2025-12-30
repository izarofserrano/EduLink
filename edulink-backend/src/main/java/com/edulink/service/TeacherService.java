package com.edulink.service;

import com.edulink.model.*;
import com.edulink.repository.*;
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

        double avgRating = documents.stream()
                .mapToDouble(doc -> {
                    Double rating = ratingRepository.getAverageRatingForDocument(doc.getDocumentId());
                    return rating != null ? rating : 0.0;
                })
                .average()
                .orElse(0.0);
        stats.setAverageRating(avgRating);

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

    // DTOs
    public static class TeacherCourseDTO {
        private Long courseId;
        private String code;
        private String courseName;
        private Integer semester;
        private Integer documentCount;
        private Integer studentCount;

        // Getters and Setters
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }

        public Integer getSemester() { return semester; }
        public void setSemester(Integer semester) { this.semester = semester; }

        public Integer getDocumentCount() { return documentCount; }
        public void setDocumentCount(Integer documentCount) { this.documentCount = documentCount; }

        public Integer getStudentCount() { return studentCount; }
        public void setStudentCount(Integer studentCount) { this.studentCount = studentCount; }
    }

    public static class CourseStatisticsDTO {
        private Long courseId;
        private String courseName;
        private String courseCode;
        private Integer totalDocuments;
        private Integer officialDocuments;
        private Integer studentDocuments;
        private Integer totalDownloads;
        private Double averageRating;
        private Integer unansweredQuestions;
        private List<DocumentStatsDTO> topDocuments;

        // Getters and Setters
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }

        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

        public Integer getTotalDocuments() { return totalDocuments; }
        public void setTotalDocuments(Integer totalDocuments) { this.totalDocuments = totalDocuments; }

        public Integer getOfficialDocuments() { return officialDocuments; }
        public void setOfficialDocuments(Integer officialDocuments) { this.officialDocuments = officialDocuments; }

        public Integer getStudentDocuments() { return studentDocuments; }
        public void setStudentDocuments(Integer studentDocuments) { this.studentDocuments = studentDocuments; }

        public Integer getTotalDownloads() { return totalDownloads; }
        public void setTotalDownloads(Integer totalDownloads) { this.totalDownloads = totalDownloads; }

        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

        public Integer getUnansweredQuestions() { return unansweredQuestions; }
        public void setUnansweredQuestions(Integer unansweredQuestions) { 
            this.unansweredQuestions = unansweredQuestions; 
        }

        public List<DocumentStatsDTO> getTopDocuments() { return topDocuments; }
        public void setTopDocuments(List<DocumentStatsDTO> topDocuments) { 
            this.topDocuments = topDocuments; 
        }
    }

    public static class DocumentStatsDTO {
        private Long documentId;
        private String title;
        private Integer downloads;
        private Double rating;

        // Getters and Setters
        public Long getDocumentId() { return documentId; }
        public void setDocumentId(Long documentId) { this.documentId = documentId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getDownloads() { return downloads; }
        public void setDownloads(Integer downloads) { this.downloads = downloads; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
    }
}