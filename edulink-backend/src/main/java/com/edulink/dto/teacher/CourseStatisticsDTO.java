package com.edulink.dto.teacher;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;


/**
 * Detailed course statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatisticsDTO {
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