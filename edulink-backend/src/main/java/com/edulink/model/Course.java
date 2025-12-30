package com.edulink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    
    @Column(nullable = false)
    private String courseName;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    private String teacherName;
    
    private Integer semester;
}
