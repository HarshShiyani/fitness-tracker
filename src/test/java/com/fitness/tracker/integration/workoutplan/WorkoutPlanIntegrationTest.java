package com.fitness.tracker.integration.workoutplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.dto.request.WorkoutPlanRequest;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.WorkoutPlanRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.profiles.active=test",
        "spring.datasource.url=jdbc:h2:mem:workoutdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
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

    private Long adminId;
    private Long userId;

    @BeforeAll
    void setupData() {
        workoutPlanRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(null, "System Admin", "admin@test.com", "Admin@123", UserRole.ADMIN, null, null, null);
        User user = new User(null, "John User", "john@test.com", "User@123", UserRole.USER, null, null, null);

        adminId = userRepository.save(admin).getId();
        userId = userRepository.save(user).getId();
    }

    @Test
    void createWorkoutPlanSuccess() throws Exception {
        WorkoutPlanRequest request = new WorkoutPlanRequest("Cardio Plan", "30 mins cardio", 30);

        mockMvc.perform(post("/api/workout-plans/user/" + userId)
                        .header("X-USER-ID", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan created successfully"))
                .andExpect(jsonPath("$.data.title").value("Cardio Plan"));

        assertThat(workoutPlanRepository.findAll()).hasSize(1);
    }

    @Test
    void createWorkoutPlanValidationFailure() throws Exception {
        WorkoutPlanRequest invalid = new WorkoutPlanRequest("", "", 0);

        mockMvc.perform(post("/api/workout-plans/user/" + userId)
                        .header("X-USER-ID", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getWorkoutPlanSuccess() throws Exception {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Strength Plan");
        plan.setDescription("Upper body strength");
        plan.setDuration(45);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        mockMvc.perform(get("/api/workout-plans/" + saved.getId() + "/user/" + userId)
                        .header("X-USER-ID", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan fetched successfully"))
                .andExpect(jsonPath("$.data.title").value("Strength Plan"));
    }

    @Test
    void getWorkoutPlanNotFound() throws Exception {
        mockMvc.perform(get("/api/workout-plans/99999/user/" + userId)
                        .header("X-USER-ID", adminId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Workout plan not found"));
    }

    @Test
    void updateWorkoutPlanSuccess() throws Exception {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Yoga Plan");
        plan.setDescription("Morning yoga");
        plan.setDuration(20);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        WorkoutPlanRequest update = new WorkoutPlanRequest("Advanced Yoga", "Morning + Evening yoga", 40);

        mockMvc.perform(put("/api/workout-plans/" + saved.getId() + "/user/" + userId)
                        .header("X-USER-ID", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan updated successfully"))
                .andExpect(jsonPath("$.data.title").value("Advanced Yoga"));

        Optional<WorkoutPlan> updated = workoutPlanRepository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getDuration()).isEqualTo(40);
    }

    @Test
    void updateWorkoutPlanNotFound() throws Exception {
        WorkoutPlanRequest update = new WorkoutPlanRequest("Ghost Plan", "Nonexistent", 15);

        mockMvc.perform(put("/api/workout-plans/12345/user/" + userId)
                        .header("X-USER-ID", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Workout plan not found"));
    }

    @Test
    void deleteWorkoutPlanSuccess() throws Exception {
        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("HIIT Plan");
        plan.setDescription("High intensity training");
        plan.setDuration(25);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        mockMvc.perform(delete("/api/workout-plans/" + saved.getId() + "/user/" + userId)
                        .header("X-USER-ID", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plan deleted successfully"));

        assertThat(workoutPlanRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void deleteWorkoutPlanNotFound() throws Exception {
        mockMvc.perform(delete("/api/workout-plans/99999/user/" + userId)
                        .header("X-USER-ID", adminId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Workout plan not found"));
    }

    @Test
    void getAllWorkoutPlansSuccess() throws Exception {
        workoutPlanRepository.deleteAll();

        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Pilates Plan");
        plan.setDescription("Core strengthening");
        plan.setDuration(35);
        plan.setUser(userRepository.findById(userId).orElseThrow());
        workoutPlanRepository.save(plan);

        mockMvc.perform(get("/api/workout-plans/user/" + userId)
                        .header("X-USER-ID", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plans fetched successfully"))
                .andExpect(jsonPath("$.data[0].title").value("Pilates Plan"));
    }

    @Test
    void getAllWorkoutPlansEmpty() throws Exception {
        workoutPlanRepository.deleteAll();

        mockMvc.perform(get("/api/workout-plans/user/" + userId)
                        .header("X-USER-ID", adminId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Workout plans fetched successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
