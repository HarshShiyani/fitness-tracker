package com.fitness.tracker.service;

import com.fitness.tracker.entity.WorkoutPlan;
import java.util.List;

public interface WorkoutPlanService {

    WorkoutPlan createWorkoutPlan(Long userId, WorkoutPlan workoutPlan);

    WorkoutPlan updateWorkoutPlan(Long id, Long userId, WorkoutPlan workoutPlan);

    void deleteWorkoutPlan(Long id, Long userId);

    WorkoutPlan getWorkoutPlan(Long id, Long userId);

    List<WorkoutPlan> getAllWorkoutPlans(Long userId);
}
