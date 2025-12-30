package com.edulink.model;

import com.edulink.model.enums.ThreadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forum_threads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumThread {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long threadId;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThreadStatus status = ThreadStatus.OPEN;
    
    @Column(nullable = false)
    private Integer views = 0;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumReply> replies = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (views == null) views = 0;
        if (status == null) status = ThreadStatus.OPEN;
    }
    
    // Helper method to check if has teacher response
    @Transient
    public boolean hasTeacherResponse() {
        return replies.stream()
                .anyMatch(reply -> reply.getUser().getRole().name().equals("TEACHER"));
    }
}