package com.fitness.tracker.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.controller.WorkoutPlanController;
import com.fitness.tracker.dto.request.WorkoutPlanRequest;
import com.fitness.tracker.dto.response.WorkoutPlanResponse;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.exception.GlobalExceptionHandler;
import com.fitness.tracker.mapper.WorkoutPlanMapper;
import com.fitness.tracker.service.WorkoutPlanService;
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

class WorkoutPlanControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkoutPlanService workoutPlanService;

    @InjectMocks
    private WorkoutPlanController workoutPlanController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final User mockUser = new User(1L, "John Doe", "john@example.com", "Password@1", UserRole.USER, null, null, null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(workoutPlanController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private final WorkoutPlanRequest validRequest =
            new WorkoutPlanRequest("Plan A", "Strength training", 30);

    private WorkoutPlan createEntity(Long id) {
        WorkoutPlan plan = WorkoutPlanMapper.toEntity(validRequest);
        plan.setId(id);
        plan.setUser(mockUser);
        plan.setCreatedDate(LocalDateTime.now());
        return plan;
    }

    @Test
    void createWorkoutPlanSuccess() throws Exception {
        WorkoutPlan planEntity = createEntity(1L);
        when(workoutPlanService.createWorkoutPlan(eq(1L), any(WorkoutPlan.class))).thenReturn(planEntity);

        mockMvc.perform(post("/api/workout-plans/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan created successfully"))
                .andExpect(jsonPath("$.data.title").value("Plan A"));
    }

    @Test
    void createWorkoutPlanValidationFailure() throws Exception {
        WorkoutPlanRequest invalidRequest = new WorkoutPlanRequest(
                "", "Description", 0 // invalid title & duration
        );

        mockMvc.perform(post("/api/workout-plans/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateWorkoutPlanSuccess() throws Exception {
        WorkoutPlan planEntity = createEntity(1L);
        when(workoutPlanService.updateWorkoutPlan(eq(1L), eq(1L), any(WorkoutPlan.class)))
                .thenReturn(planEntity);

        mockMvc.perform(put("/api/workout-plans/1/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan updated successfully"))
                .andExpect(jsonPath("$.data.duration").value(30));
    }

    @Test
    void updateWorkoutPlanValidationFailure() throws Exception {
        WorkoutPlanRequest invalidRequest = new WorkoutPlanRequest(
                "", "Some desc", -5
        );

        mockMvc.perform(put("/api/workout-plans/1/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteWorkoutPlanSuccess() throws Exception {
        mockMvc.perform(delete("/api/workout-plans/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan deleted successfully"));
    }

    @Test
    void getWorkoutPlanSuccess() throws Exception {
        WorkoutPlan planEntity = createEntity(1L);
        when(workoutPlanService.getWorkoutPlan(1L, 1L)).thenReturn(planEntity);

        mockMvc.perform(get("/api/workout-plans/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("Plan A"));
    }

    @Test
    void getAllWorkoutPlansSuccess() throws Exception {
        WorkoutPlan planEntity = createEntity(1L);
        when(workoutPlanService.getAllWorkoutPlans(1L)).thenReturn(List.of(planEntity));

        mockMvc.perform(get("/api/workout-plans/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plans fetched successfully"))
                .andExpect(jsonPath("$.data[0].title").value("Plan A"));
    }
}
