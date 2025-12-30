package com.edulink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "document_ratings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;
    
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(nullable = false)
    private LocalDateTime ratedAt;
    
    @PrePersist
    protected void onCreate() {
        ratedAt = LocalDateTime.now();
    }
}

