package com.fitness.tracker.controller;

import com.fitness.tracker.dto.BaseResponse;
import com.fitness.tracker.dto.request.WorkoutPlanRequest;
import com.fitness.tracker.dto.response.WorkoutPlanResponse;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.mapper.WorkoutPlanMapper;
import com.fitness.tracker.service.WorkoutPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workout-plans")
@Tag(name = "Workout Plan Management", description = "Create, Get, Update & Delete the workout plans")
@SecurityRequirement(name = "bearerAuth")
public class WorkoutPlanController {

    private final WorkoutPlanService workoutPlanService;

    public WorkoutPlanController(WorkoutPlanService workoutPlanService) {
        this.workoutPlanService = workoutPlanService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Create a workout plan", description = "Creates a workout plan for a specific user")
    @ApiResponse(responseCode = "200", description = "Workout plan created successfully")
    @PostMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<WorkoutPlanResponse>> createWorkoutPlan(
            @PathVariable Long userId,
            @Valid @RequestBody WorkoutPlanRequest request) {

        WorkoutPlan workoutPlan = WorkoutPlanMapper.toEntity(request);
        WorkoutPlan saved = workoutPlanService.createWorkoutPlan(userId, workoutPlan);

        return ResponseEntity.ok(
                new BaseResponse<>("Workout plan created successfully", WorkoutPlanMapper.toResponse(saved))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Update a workout plan", description = "Updates an existing workout plan of a specific user")
    @ApiResponse(responseCode = "200", description = "Workout plan updated successfully")
    @PutMapping("/{workoutId}/user/{userId}")
    public ResponseEntity<BaseResponse<WorkoutPlanResponse>> updateWorkoutPlan(
            @PathVariable Long workoutId,
            @PathVariable Long userId,
            @Valid @RequestBody WorkoutPlanRequest request) {

        WorkoutPlan workoutPlan = WorkoutPlanMapper.toEntity(request);
        WorkoutPlan updated = workoutPlanService.updateWorkoutPlan(workoutId, userId, workoutPlan);

        return ResponseEntity.ok(
                new BaseResponse<>("Workout plan updated successfully", WorkoutPlanMapper.toResponse(updated))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Delete a workout plan", description = "Deletes a workout plan of a specific user")
    @ApiResponse(responseCode = "200", description = "Workout plan deleted successfully")
    @DeleteMapping("/{workoutId}/user/{userId}")
    public ResponseEntity<BaseResponse<Void>> deleteWorkoutPlan(
            @PathVariable Long workoutId,
            @PathVariable Long userId) {

        workoutPlanService.deleteWorkoutPlan(workoutId, userId);
        return ResponseEntity.ok(new BaseResponse<>("Workout plan deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get a workout plan", description = "Fetch a workout plan by its ID and user")
    @ApiResponse(responseCode = "200", description = "Workout plan fetched successfully")
    @GetMapping("/{workoutId}/user/{userId}")
    public ResponseEntity<BaseResponse<WorkoutPlanResponse>> getWorkoutPlan(
            @PathVariable Long workoutId,
            @PathVariable Long userId) {

        WorkoutPlan workoutPlan = workoutPlanService.getWorkoutPlan(workoutId, userId);
        return ResponseEntity.ok(
                new BaseResponse<>("Workout plan fetched successfully", WorkoutPlanMapper.toResponse(workoutPlan))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all workout plans", description = "Fetch all workout plans for a user")
    @ApiResponse(responseCode = "200", description = "Workout plans fetched successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<WorkoutPlanResponse>>> getAllWorkoutPlans(
            @PathVariable Long userId) {

        List<WorkoutPlanResponse> responses = workoutPlanService.getAllWorkoutPlans(userId).stream()
                .map(WorkoutPlanMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new BaseResponse<>("Workout plans fetched successfully", responses)
        );
    }
}
