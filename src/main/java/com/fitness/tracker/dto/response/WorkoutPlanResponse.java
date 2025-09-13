package com.fitness.tracker.dto.response;

import com.fitness.tracker.enums.UserRole;
import java.time.LocalDateTime;

public record WorkoutPlanResponse(
        Long id,
        String title,
        String description,
        int duration,
        LocalDateTime createdDate,
        Long userId,
        String userName,
        UserRole userRole
) {}
