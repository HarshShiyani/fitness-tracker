package com.fitness.tracker.service;

import com.fitness.tracker.entity.ActivityLog;

import java.util.List;

public interface ActivityLogService {

    ActivityLog createActivityLog(Long userId, Long workoutPlanId, ActivityLog activityLog);

    ActivityLog updateActivityLog(Long id, Long userId, Long workoutPlanId, ActivityLog activityLog);

    void deleteActivityLog(Long id, Long userId);

    ActivityLog getActivityLog(Long id, Long userId);

    List<ActivityLog> getAllActivityLogsByUser(Long userId);

    List<ActivityLog> getAllActivityLogsByWorkoutPlan(Long workoutPlanId);
}
