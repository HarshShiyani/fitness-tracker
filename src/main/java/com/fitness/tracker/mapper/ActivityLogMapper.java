package com.fitness.tracker.mapper;

import com.fitness.tracker.dto.request.ActivityLogRequest;
import com.fitness.tracker.dto.response.ActivityLogResponse;
import com.fitness.tracker.entity.ActivityLog;

public class ActivityLogMapper {

    private ActivityLogMapper() {}

    public static ActivityLog toEntity(ActivityLogRequest request) {
        ActivityLog log = new ActivityLog();
        log.setActivityType(request.activityType());
        log.setCaloriesBurned(request.caloriesBurned());
        log.setDuration(request.duration());
        return log;
    }

    public static ActivityLogResponse toResponse(ActivityLog log) {
        return new ActivityLogResponse(
                log.getId(),
                log.getActivityType(),
                log.getCaloriesBurned(),
                log.getDuration(),
                log.getCreatedDate(),
                log.getUser() != null ? log.getUser().getId() : null,
                log.getUser() != null ? log.getUser().getName() : null,
                log.getUser() != null ? log.getUser().getRole() : null,
                log.getWorkoutPlan() != null ? log.getWorkoutPlan().getId() : null,
                log.getWorkoutPlan() != null ? log.getWorkoutPlan().getTitle() : null
        );
    }
}
