package com.fitness.tracker.integration.workoutplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.dto.request.WorkoutPlanRequest;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.WorkoutPlanRepository;
import com.fitness.tracker.utils.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkoutPlanIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String jwtToken;
    private Long userId;

    @BeforeAll
    void setupData() {
        workoutPlanRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(null, "System Admin", "admin@test.com", "Admin@123", UserRole.ADMIN, null, null, null);
        User user = new User(null, "John User", "john@test.com", "User@123", UserRole.USER, null, null, null);

        userRepository.saveAll(List.of(admin, user));
        userId = user.getId();

        jwtToken = jwtUtil.generateToken(admin.getEmail(), List.of(admin.getRole().name()));
    }

    @Test
    void createWorkoutPlanSuccess() throws Exception {
        WorkoutPlanRequest request = new WorkoutPlanRequest("Cardio Plan", "30 minutes cardio", 30);

        mockMvc.perform(post("/api/workout-plans/user/" + userId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Workout plan created successfully"))
            .andExpect(jsonPath("$.data.title").value("Cardio Plan"));

        assertThat(workoutPlanRepository.findAll()).isNotEmpty();
    }

    @Test
    void updateWorkoutPlanSuccess() throws Exception {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Old Plan");
        plan.setDescription("desc");
        plan.setDuration(20);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        WorkoutPlanRequest update = new WorkoutPlanRequest("Updated Plan", "New desc", 45);

        mockMvc.perform(put("/api/workout-plans/" + saved.getId() + "/user/" + userId)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Workout plan updated successfully"))
            .andExpect(jsonPath("$.data.title").value("Updated Plan"));
    }

    @Test
    void deleteWorkoutPlanSuccess() throws Exception {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("To Delete");
        plan.setDescription("desc");
        plan.setDuration(15);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        mockMvc.perform(delete("/api/workout-plans/" + saved.getId() + "/user/" + userId)
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Workout plan deleted successfully"));

        assertThat(workoutPlanRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void getWorkoutPlanSuccess() throws Exception {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Get Plan");
        plan.setDescription("desc");
        plan.setDuration(25);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        mockMvc.perform(get("/api/workout-plans/" + saved.getId() + "/user/" + userId)
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Workout plan fetched successfully"))
            .andExpect(jsonPath("$.data.title").value("Get Plan"));
    }

    @Test
    void getAllWorkoutPlansSuccess() throws Exception {
        workoutPlanRepository.deleteAll();

        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Bulk Plan");
        plan.setDescription("desc");
        plan.setDuration(40);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        workoutPlanRepository.save(plan);

        mockMvc.perform(get("/api/workout-plans/user/" + userId)
                .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Workout plans fetched successfully"))
            .andExpect(jsonPath("$.data[0].title").value("Bulk Plan"));
    }
}