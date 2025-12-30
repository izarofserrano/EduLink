package com.edulink.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumThreadDTO {
    private Long threadId;
    private String title;
    private String content;
    private String status;
    private Integer views;
    private Integer replyCount;
    private Boolean hasTeacherResponse;
    private LocalDateTime createdAt;
    
    // Author info
    private Long authorId;
    private String authorUsername;
    private String authorRole;
    
    // Course info
    private Long courseId;
    private String courseName;
    private String courseCode;
}

