package com.edulink.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Forum Thread Detail DTO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumThreadDetailDTO {
    private Long threadId;
    private String title;
    private String content;
    private String status;
    private Integer views;
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
    
    // Replies
    private List<ForumReplyDTO> replies;
}


