package com.edulink.dto.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

/**
 * Document DTO
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long documentId;
    private String docTitle;
    private String docDescription;
    private String documentType;
    private LocalDateTime uploadedAt;
    private Boolean isApproved;
    private Integer downloadCount;
    private Double averageRating;
    private Long totalRatings;
    
    // Course info
    private Long courseId;
    private String courseName;
    
    // Uploader info
    private Long uploaderId;
    private String uploaderUsername;
}



