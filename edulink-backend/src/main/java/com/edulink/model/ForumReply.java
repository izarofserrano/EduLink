package com.edulink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_replies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumReply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    private ForumThread thread;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Boolean isTeacherResponse = false;  
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isTeacherResponse == null) {
            isTeacherResponse = false;
        }
    }
}