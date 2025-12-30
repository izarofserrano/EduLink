package com.edulink.dto.course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long courseId;
    private String code;
    private String courseName;
    private String teacherName;
    private Integer semester;
    private String description;
}



