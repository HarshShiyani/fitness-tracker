package com.fitness.tracker.mapper;

import com.fitness.tracker.dto.request.WorkoutPlanRequest;
import com.fitness.tracker.dto.response.WorkoutPlanResponse;
import com.fitness.tracker.entity.WorkoutPlan;

public class WorkoutPlanMapper {

    public static WorkoutPlan toEntity(WorkoutPlanRequest request) {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle(request.title());
        plan.setDescription(request.description());
        plan.setDuration(request.duration());
        return plan;
    }

    public static WorkoutPlanResponse toResponse(WorkoutPlan plan) {
        return new WorkoutPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getDescription(),
                plan.getDuration(),
                plan.getCreatedDate(),
                plan.getUser().getId(),
                plan.getUser().getName(),
                plan.getUser().getRole()
        );
    }
}
