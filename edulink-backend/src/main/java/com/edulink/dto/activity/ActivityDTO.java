package com.edulink.dto.activity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    private Long activityId;
    private String title;
    private String description;
    private String activityType;
    private LocalDateTime activityDate;
    private String location;
    private Integer attendance;
    
    // Organizer info
    private Long organizerId;
    private String organizerUsername;
    private String organizerRole;
}



