package com.edulink.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends User {
    
    @Column(nullable = false)
    private Double reputationPoints = 0.0;
}
