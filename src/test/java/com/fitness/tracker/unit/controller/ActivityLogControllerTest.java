package com.fitness.tracker.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.controller.ActivityLogController;
import com.fitness.tracker.dto.request.ActivityLogRequest;
import com.fitness.tracker.dto.response.ActivityLogResponse;
import com.fitness.tracker.entity.ActivityLog;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.exception.GlobalExceptionHandler;
import com.fitness.tracker.mapper.ActivityLogMapper;
import com.fitness.tracker.service.ActivityLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ActivityLogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private ActivityLogController activityLogController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ActivityLogRequest validRequest;
    private ActivityLog validEntity;
    private ActivityLogResponse validResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(activityLogController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        validRequest = new ActivityLogRequest("Running", 200, 30);

        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setRole(UserRole.USER);

        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setId(10L);
        workoutPlan.setTitle("Cardio Plan");

        validEntity = ActivityLogMapper.toEntity(validRequest);
        validEntity.setId(100L);
        validEntity.setUser(user);
        validEntity.setWorkoutPlan(workoutPlan);
        validEntity.setCreatedDate(LocalDateTime.now());

        validResponse = ActivityLogMapper.toResponse(validEntity);
    }

    @Test
    void createActivityLogSuccess() throws Exception {
        when(activityLogService.createActivityLog(eq(1L), eq(10L), any(ActivityLog.class)))
                .thenReturn(validEntity);

        mockMvc.perform(post("/api/activity-logs")
                        .param("userId", "1")
                        .param("workoutPlanId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log created successfully"))
                .andExpect(jsonPath("$.data.activityType").value("Running"));
    }

    @Test
    void createActivityLogValidationFailure() throws Exception {
        ActivityLogRequest invalid = new ActivityLogRequest("", 0, 0);

        mockMvc.perform(post("/api/activity-logs")
                        .param("userId", "1")
                        .param("workoutPlanId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateActivityLogSuccess() throws Exception {
        when(activityLogService.updateActivityLog(eq(100L), eq(1L), eq(10L), any(ActivityLog.class)))
                .thenReturn(validEntity);

        mockMvc.perform(put("/api/activity-logs/100")
                        .param("userId", "1")
                        .param("workoutPlanId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log updated successfully"))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    void deleteActivityLogSuccess() throws Exception {
        mockMvc.perform(delete("/api/activity-logs/100")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log deleted successfully"));
    }

    @Test
    void getActivityLogSuccess() throws Exception {
        when(activityLogService.getActivityLog(100L, 1L)).thenReturn(validEntity);

        mockMvc.perform(get("/api/activity-logs/100")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log fetched successfully"))
                .andExpect(jsonPath("$.data.activityType").value("Running"));
    }

    @Test
    void getAllByUserSuccess() throws Exception {
        when(activityLogService.getAllActivityLogsByUser(1L)).thenReturn(List.of(validEntity));

        mockMvc.perform(get("/api/activity-logs/by-user")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity logs fetched successfully"))
                .andExpect(jsonPath("$.data[0].activityType").value("Running"));
    }

    @Test
    void getAllByWorkoutPlanSuccess() throws Exception {
        when(activityLogService.getAllActivityLogsByWorkoutPlan(10L)).thenReturn(List.of(validEntity));

        mockMvc.perform(get("/api/activity-logs/by-workout")
                        .param("workoutPlanId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity logs fetched successfully"))
                .andExpect(jsonPath("$.data[0].workoutPlanTitle").value("Cardio Plan"));
    }
}
