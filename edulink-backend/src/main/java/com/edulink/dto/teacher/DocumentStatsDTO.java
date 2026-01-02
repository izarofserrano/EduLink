package com.edulink.dto.teacher;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


/**
 * Document statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatsDTO {
    private Long documentId;
    private String title;
    private String documentType;
    private Integer downloads;
    private Double rating;
    private Long totalRatings;
    private LocalDateTime uploadedAt;
    private String uploaderUsername;
}



