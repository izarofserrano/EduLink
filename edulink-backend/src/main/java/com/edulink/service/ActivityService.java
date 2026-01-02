package com.edulink.service;


import com.edulink.dto.activity.ActivityCreateRequest;
import com.edulink.dto.activity.ActivityDTO;
import com.edulink.dto.activity.ActivityUpdateRequest;
import com.edulink.exception.BusinessException;
import com.edulink.model.Activity;
import com.edulink.model.User;
import com.edulink.model.enums.ActivityType;
import com.edulink.repository.ActivityRepository;
import com.edulink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {


    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);


    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;


    /**
     * Get all activities
     */
    @Transactional(readOnly = true)
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Get activity by ID
     */
    @Transactional(readOnly = true)
    public ActivityDTO getActivityById(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> BusinessException.notFound("Activity not found with id: " + activityId));
        
        return convertToDTO(activity);
    }


    /**
     * Create new activity
     */
    public ActivityDTO createActivity(ActivityCreateRequest request, String username) {
        logger.info("User {} creating activity: {}", username, request.getTitle());


        // Get organizer
        User organizer = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        // Validate activity type
        ActivityType activityType;
        try {
            activityType = ActivityType.valueOf(request.getActivityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid activity type: " + request.getActivityType());
        }

        
        // Create activity
        Activity activity = new Activity();
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setActivityType(activityType);
        activity.setActivityDate(request.getActivityDate());
        activity.setLocation(request.getLocation());
        activity.setOrganizer(organizer);
        activity.setAttendance(0);


        Activity savedActivity = activityRepository.save(activity);
        logger.info("Activity created with ID: {}", savedActivity.getActivityId());


        return convertToDTO(savedActivity);
    }


    /**
     * Update activity
     */
    public ActivityDTO updateActivity(Long activityId, ActivityUpdateRequest request, String username) {
        logger.info("User {} updating activity {}", username, activityId);


        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> BusinessException.notFound("Activity not found with id: " + activityId));


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        // Only organizer can update
        if (!activity.getOrganizer().getUserId().equals(user.getUserId())) {
            throw BusinessException.forbidden("Only the organizer can update this activity");
        }


        // Update fields if provided
        if (request.getTitle() != null) {
            activity.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            activity.setDescription(request.getDescription());
        }
        if (request.getActivityType() != null) {
            try {
                activity.setActivityType(ActivityType.valueOf(request.getActivityType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid activity type: " + request.getActivityType());
            }
        }
        if (request.getActivityDate() != null) {
            activity.setActivityDate(request.getActivityDate());
        }
        if (request.getLocation() != null) {
            activity.setLocation(request.getLocation());
        }
        if (request.getAttendance() != null) {
            activity.setAttendance(request.getAttendance());
        }


        Activity updatedActivity = activityRepository.save(activity);
        return convertToDTO(updatedActivity);
    }


    /**
     * Delete activity
     */
    public void deleteActivity(Long activityId, String username) {
        logger.info("User {} deleting activity {}", username, activityId);


        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> BusinessException.notFound("Activity not found with id: " + activityId));


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        // Only organizer can delete
        if (!activity.getOrganizer().getUserId().equals(user.getUserId())) {
            throw BusinessException.forbidden("Only the organizer can delete this activity");
        }


        activityRepository.delete(activity);
        logger.info("Activity {} deleted", activityId);
    }


    // Helper methods

    private ActivityDTO convertToDTO(Activity activity) {
        return ActivityDTO.builder()
                .activityId(activity.getActivityId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .activityType(activity.getActivityType().toString())
                .activityDate(activity.getActivityDate())
                .location(activity.getLocation())
                .attendance(activity.getAttendance())
                .organizerId(activity.getOrganizer().getUserId())
                .organizerUsername(activity.getOrganizer().getUsername())
                .organizerRole(activity.getOrganizer().getRole().toString())
                .build();
    }
}



