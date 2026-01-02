package com.edulink.dto.teacher;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Teacher course overview DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseDTO {
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
