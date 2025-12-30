package com.edulink.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
    // Teachers inherit all properties from User
}
