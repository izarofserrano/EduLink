package com.edulink.dto.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Forum Reply DTO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumReplyDTO {
    private Long replyId;
    private String content;
    private Boolean isTeacherResponse;
    private LocalDateTime createdAt;
    
    // User info
    private Long userId;
    private String username;
    private String userRole;
    
    // Thread info
    private Long threadId;
}


