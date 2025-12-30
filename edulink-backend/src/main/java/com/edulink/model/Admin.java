package com.edulink.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admin")
@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    // Admins inherit all properties from User
}
