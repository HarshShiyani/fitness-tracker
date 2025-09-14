package com.fitness.tracker.controller;

import com.fitness.tracker.dto.BaseResponse;
import com.fitness.tracker.dto.request.ActivityLogRequest;
import com.fitness.tracker.dto.response.ActivityLogResponse;
import com.fitness.tracker.entity.ActivityLog;
import com.fitness.tracker.mapper.ActivityLogMapper;
import com.fitness.tracker.service.ActivityLogService;
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
@RequestMapping("/api/activity-logs")
@Tag(name = "Activity Log Management", description = "Create, Get, Update & Delete activity logs")
@SecurityRequirement(name = "X-USER-ID")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Create activity log", description = "Creates an activity log for a user and workout plan")
    @ApiResponse(responseCode = "200", description = "Activity log created successfully")
    @PostMapping
    public ResponseEntity<BaseResponse<ActivityLogResponse>> createActivityLog(
            @RequestParam Long userId,
            @RequestParam Long workoutPlanId,
            @Valid @RequestBody ActivityLogRequest request
    ) {
        ActivityLog log = ActivityLogMapper.toEntity(request);
        ActivityLog saved = activityLogService.createActivityLog(userId, workoutPlanId, log);
        return ResponseEntity.ok(new BaseResponse<>(
                "Activity log created successfully",
                ActivityLogMapper.toResponse(saved)
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Update activity log", description = "Updates an existing activity log")
    @ApiResponse(responseCode = "200", description = "Activity log updated successfully")
    @PutMapping("/{activityLogId}")
    public ResponseEntity<BaseResponse<ActivityLogResponse>> updateActivityLog(
            @PathVariable Long activityLogId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long workoutPlanId,
            @Valid @RequestBody ActivityLogRequest request
    ) {
        ActivityLog log = ActivityLogMapper.toEntity(request);
        ActivityLog updated = activityLogService.updateActivityLog(activityLogId, userId, workoutPlanId, log);
        return ResponseEntity.ok(new BaseResponse<>(
                "Activity log updated successfully",
                ActivityLogMapper.toResponse(updated)
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Delete activity log", description = "Deletes an activity log by ID and user")
    @ApiResponse(responseCode = "200", description = "Activity log deleted successfully")
    @DeleteMapping("/{activityLogId}")
    public ResponseEntity<BaseResponse<Void>> deleteActivityLog(
            @PathVariable Long activityLogId,
            @RequestParam Long userId
    ) {
        activityLogService.deleteActivityLog(activityLogId, userId);
        return ResponseEntity.ok(new BaseResponse<>("Activity log deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get activity log", description = "Fetch a single activity log by ID and user")
    @ApiResponse(responseCode = "200", description = "Activity log fetched successfully")
    @GetMapping("/{activityLogId}")
    public ResponseEntity<BaseResponse<ActivityLogResponse>> getActivityLog(
            @PathVariable Long activityLogId,
            @RequestParam Long userId
    ) {
        ActivityLog log = activityLogService.getActivityLog(activityLogId, userId);
        return ResponseEntity.ok(new BaseResponse<>(
                "Activity log fetched successfully",
                ActivityLogMapper.toResponse(log)
        ));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all activity logs by user", description = "Fetch all activity logs for a user")
    @ApiResponse(responseCode = "200", description = "Activity logs fetched successfully")
    @GetMapping("/by-user")
    public ResponseEntity<BaseResponse<List<ActivityLogResponse>>> getAllByUser(
            @RequestParam Long userId
    ) {
        List<ActivityLogResponse> responses = activityLogService.getAllActivityLogsByUser(userId)
                .stream()
                .map(ActivityLogMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new BaseResponse<>("Activity logs fetched successfully", responses));
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all activity logs by workout plan", description = "Fetch all activity logs for a workout plan")
    @ApiResponse(responseCode = "200", description = "Activity logs fetched successfully")
    @GetMapping("/by-workout")
    public ResponseEntity<BaseResponse<List<ActivityLogResponse>>> getAllByWorkoutPlan(
            @RequestParam Long workoutPlanId
    ) {
        List<ActivityLogResponse> responses = activityLogService.getAllActivityLogsByWorkoutPlan(workoutPlanId)
                .stream()
                .map(ActivityLogMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new BaseResponse<>("Activity logs fetched successfully", responses));
    }
}
