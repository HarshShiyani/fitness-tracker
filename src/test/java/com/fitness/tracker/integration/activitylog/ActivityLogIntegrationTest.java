package com.fitness.tracker.integration.activitylog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.dto.request.ActivityLogRequest;
import com.fitness.tracker.entity.ActivityLog;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.repository.ActivityLogRepository;
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
        "spring.datasource.url=jdbc:h2:mem:activitydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActivityLogIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private Long adminId;
    private Long userId;
    private Long workoutPlanId;

    @BeforeAll
    void setupData() {
        activityLogRepository.deleteAll();
        workoutPlanRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User(null, "System Admin", "admin@test.com", "Admin@123", UserRole.ADMIN, null, null, null);
        User user = new User(null, "John User", "john@test.com", "User@123", UserRole.USER, null, null, null);

        adminId = userRepository.save(admin).getId();
        userId = userRepository.save(user).getId();

        WorkoutPlan plan = new WorkoutPlan();
        plan.setTitle("Cardio Plan");
        plan.setDescription("30 mins cardio");
        plan.setDuration(30);
        plan.setUser(user);

        workoutPlanId = workoutPlanRepository.save(plan).getId();
    }

    @Test
    void createActivityLogSuccess() throws Exception {
        ActivityLogRequest request = new ActivityLogRequest("Running", 200, 30);

        mockMvc.perform(post("/api/activity-logs")
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString())
                        .param("workoutPlanId", workoutPlanId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log created successfully"))
                .andExpect(jsonPath("$.data.activityType").value("Running"));

        assertThat(activityLogRepository.findAll()).isNotEmpty();
    }

    @Test
    void createActivityLogValidationFailure() throws Exception {
        ActivityLogRequest invalid = new ActivityLogRequest("", 0, 0);

        mockMvc.perform(post("/api/activity-logs")
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString())
                        .param("workoutPlanId", workoutPlanId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getActivityLogSuccess() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setActivityType("Cycling");
        log.setCaloriesBurned(150);
        log.setDuration(25);
        log.setUser(userRepository.findById(userId).orElseThrow());
        log.setWorkoutPlan(workoutPlanRepository.findById(workoutPlanId).orElseThrow());
        ActivityLog saved = activityLogRepository.save(log);

        mockMvc.perform(get("/api/activity-logs/" + saved.getId())
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log fetched successfully"))
                .andExpect(jsonPath("$.data.activityType").value("Cycling"));
    }

    @Test
    void getActivityLogNotFound() throws Exception {
        mockMvc.perform(get("/api/activity-logs/99999")
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Activity log not found"));
    }

    @Test
    void updateActivityLogSuccess() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setActivityType("Walking");
        log.setCaloriesBurned(100);
        log.setDuration(15);
        log.setUser(userRepository.findById(userId).orElseThrow());
        log.setWorkoutPlan(workoutPlanRepository.findById(workoutPlanId).orElseThrow());
        ActivityLog saved = activityLogRepository.save(log);

        ActivityLogRequest update = new ActivityLogRequest("Brisk Walking", 120, 20);

        mockMvc.perform(put("/api/activity-logs/" + saved.getId())
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString())
                        .param("workoutPlanId", workoutPlanId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log updated successfully"))
                .andExpect(jsonPath("$.data.activityType").value("Brisk Walking"));

        Optional<ActivityLog> updated = activityLogRepository.findById(saved.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getCaloriesBurned()).isEqualTo(120);
    }

    @Test
    void deleteActivityLogSuccess() throws Exception {
        ActivityLog log = new ActivityLog();
        log.setActivityType("Swimming");
        log.setCaloriesBurned(300);
        log.setDuration(40);
        log.setUser(userRepository.findById(userId).orElseThrow());
        log.setWorkoutPlan(workoutPlanRepository.findById(workoutPlanId).orElseThrow());
        ActivityLog saved = activityLogRepository.save(log);

        mockMvc.perform(delete("/api/activity-logs/" + saved.getId())
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity log deleted successfully"));

        assertThat(activityLogRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void getAllByUserSuccess() throws Exception {
        activityLogRepository.deleteAll();

        ActivityLog log = new ActivityLog();
        log.setActivityType("Jump Rope");
        log.setCaloriesBurned(250);
        log.setDuration(20);
        log.setUser(userRepository.findById(userId).orElseThrow());
        log.setWorkoutPlan(workoutPlanRepository.findById(workoutPlanId).orElseThrow());
        activityLogRepository.save(log);

        mockMvc.perform(get("/api/activity-logs/by-user")
                        .header("X-USER-ID", adminId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity logs fetched successfully"))
                .andExpect(jsonPath("$.data[0].activityType").value("Jump Rope"));
    }

    @Test
    void getAllByWorkoutPlanSuccess() throws Exception {
        activityLogRepository.deleteAll();

        ActivityLog log = new ActivityLog();
        log.setActivityType("Rowing");
        log.setCaloriesBurned(180);
        log.setDuration(35);
        log.setUser(userRepository.findById(userId).orElseThrow());
        log.setWorkoutPlan(workoutPlanRepository.findById(workoutPlanId).orElseThrow());
        activityLogRepository.save(log);

        mockMvc.perform(get("/api/activity-logs/by-workout")
                        .header("X-USER-ID", adminId)
                        .param("workoutPlanId", workoutPlanId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Activity logs fetched successfully"))
                .andExpect(jsonPath("$.data[0].activityType").value("Rowing"));
    }
}
