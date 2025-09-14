package com.fitness.tracker.dto.response;

import com.fitness.tracker.enums.UserRole;
import java.time.LocalDateTime;

public record ActivityLogResponse(
        Long id,
        String activityType,
        int caloriesBurned,
        int duration,
        LocalDateTime createdDate,
        Long userId,
        String userName,
        UserRole userRole,
        Long workoutPlanId,
        String workoutPlanTitle
) {}
