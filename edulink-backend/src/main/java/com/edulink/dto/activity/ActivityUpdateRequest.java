package com.edulink.dto.activity;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUpdateRequest {
    
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;
    
    private String activityType;
    
    private LocalDateTime activityDate;
    
    @Size(min = 3, max = 200, message = "Location must be between 3 and 200 characters")
    private String location;
    
    private Integer attendance;
}



