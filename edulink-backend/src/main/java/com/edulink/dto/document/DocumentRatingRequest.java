package com.edulink.dto.document;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;


/***
 * Document Rating Request DTO
 */
public class DocumentRatingRequest {

    @Min(1)
    @Max(5)
    private Integer rating;
    
}
