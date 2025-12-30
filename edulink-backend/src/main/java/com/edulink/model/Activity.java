package com.edulink.model;

import com.edulink.model.enums.ActivityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;
    
    private LocalDateTime activityDate;

    private String location;  

    
    private Integer attendance;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Transient
    public String getOrganizerName() {
        return organizer != null ? organizer.getUsername() : "Unknown";
    }
}
