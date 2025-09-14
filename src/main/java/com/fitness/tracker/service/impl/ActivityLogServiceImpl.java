package com.fitness.tracker.service.impl;

import com.fitness.tracker.entity.ActivityLog;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.exception.CustomException;
import com.fitness.tracker.repository.ActivityLogRepository;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.WorkoutPlanRepository;
import com.fitness.tracker.service.ActivityLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;
    private final WorkoutPlanRepository workoutPlanRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository,
        UserRepository userRepository,
        WorkoutPlanRepository workoutPlanRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
        this.workoutPlanRepository = workoutPlanRepository;
    }

    @Override
    public ActivityLog createActivityLog(Long userId, Long workoutPlanId, ActivityLog activityLog) {
        log.debug("Creating activity log for user '{}' and workout plan '{}'", userId, workoutPlanId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Workout plan not found"));

        activityLog.setUser(user);
        activityLog.setWorkoutPlan(workoutPlan);

        ActivityLog saved = activityLogRepository.save(activityLog);
        log.info("Activity log '{}' created successfully for user '{}'", saved.getId(), userId);
        return saved;
    }

    @Override
    public ActivityLog updateActivityLog(Long id, Long userId, Long workoutPlanId, ActivityLog activityLog) {
        log.debug("Updating activity log '{}'", id);

        ActivityLog existing = activityLogRepository.findById(id)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Activity log not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Activity log does not belong to this user");
        }

        existing.setActivityType(activityLog.getActivityType());
        existing.setCaloriesBurned(activityLog.getCaloriesBurned());
        existing.setDuration(activityLog.getDuration());

        if (workoutPlanId != null) {
            WorkoutPlan workoutPlan = workoutPlanRepository.findById(workoutPlanId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Workout plan not found"));
            existing.setWorkoutPlan(workoutPlan);
        }

        ActivityLog updated = activityLogRepository.save(existing);
        log.info("Activity log '{}' updated successfully", updated.getId());
        return updated;
    }

    @Override
    public void deleteActivityLog(Long id, Long userId) {
        log.debug("Deleting activity log '{}' for user '{}'", id, userId);

        ActivityLog existing = activityLogRepository.findById(id)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Activity log not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Activity log does not belong to this user");
        }

        activityLogRepository.delete(existing);
        log.info("Activity log '{}' deleted successfully", id);
    }

    @Override
    public ActivityLog getActivityLog(Long id, Long userId) {
        log.debug("Fetching activity log '{}' for user '{}'", id, userId);

        ActivityLog existing = activityLogRepository.findById(id)
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Activity log not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Activity log does not belong to this user");
        }

        return existing;
    }

    @Override
    public List<ActivityLog> getAllActivityLogsByUser(Long userId) {
        log.debug("Fetching all activity logs for user '{}'", userId);
        return activityLogRepository.findByUserId(userId);
    }

    @Override
    public List<ActivityLog> getAllActivityLogsByWorkoutPlan(Long workoutPlanId) {
        log.debug("Fetching all activity logs for workout plan '{}'", workoutPlanId);
        return activityLogRepository.findByWorkoutPlanId(workoutPlanId);
    }
}
