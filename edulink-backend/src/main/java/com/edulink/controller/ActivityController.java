package com.edulink.controller;


import com.edulink.dto.activity.ActivityCreateRequest;
import com.edulink.dto.activity.ActivityDTO;
import com.edulink.dto.activity.ActivityUpdateRequest;
import com.edulink.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ActivityController {


    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    
    private final ActivityService activityService;


    /**
     * Get all activities
     */
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        List<ActivityDTO> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }


    /**
     * Get activity by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        ActivityDTO activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }


    /**
     * Create new activity (authenticated users only)
     */
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(
            @Valid @RequestBody ActivityCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} creating activity", userDetails.getUsername());
        ActivityDTO activity = activityService.createActivity(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }


    /**
     * Update activity (organizer only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} updating activity {}", userDetails.getUsername(), id);
        ActivityDTO activity = activityService.updateActivity(id, request, userDetails.getUsername());
        return ResponseEntity.ok(activity);
    }


    /**
     * Delete activity (organizer only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} deleting activity {}", userDetails.getUsername(), id);
        activityService.deleteActivity(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Activity deleted successfully"));
    }
}



