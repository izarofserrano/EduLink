package com.edulink.dto.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailDTO {
    private Long documentId;
    private String docTitle;
    private String docDescription;
    private String documentType;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    private Boolean isApproved;
    private Integer downloadCount;
    
    // Rating info
    private Double averageRating;
    private Long totalRatings;
    private Integer userRating; // Current user's rating
    
    // Course info
    private Long courseId;
    private String courseName;
    private String courseCode;
    
    // Uploader info
    private Long uploaderId;
    private String uploaderUsername;
    private String uploaderRole;
}



