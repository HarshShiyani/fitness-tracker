package com.fitness.tracker.unit.service;

import com.fitness.tracker.entity.ActivityLog;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.exception.CustomException;
import com.fitness.tracker.repository.ActivityLogRepository;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.WorkoutPlanRepository;
import com.fitness.tracker.service.impl.ActivityLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ActivityLogServiceImplTest {

    @Mock
    private ActivityLogRepository activityLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @InjectMocks
    private ActivityLogServiceImpl activityLogService;

    private User user;
    private WorkoutPlan workoutPlan;
    private ActivityLog activityLog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setRole(UserRole.USER);

        workoutPlan = new WorkoutPlan();
        workoutPlan.setId(10L);
        workoutPlan.setTitle("Cardio Plan");

        activityLog = new ActivityLog();
        activityLog.setId(100L);
        activityLog.setActivityType("Running");
        activityLog.setCaloriesBurned(200);
        activityLog.setDuration(30);
        activityLog.setUser(user);
        activityLog.setWorkoutPlan(workoutPlan);
    }

    @Test
    void createActivityLogSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(workoutPlanRepository.findById(10L)).thenReturn(Optional.of(workoutPlan));
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(activityLog);

        ActivityLog saved = activityLogService.createActivityLog(1L, 10L, activityLog);

        assertThat(saved.getId()).isEqualTo(100L);
        assertThat(saved.getActivityType()).isEqualTo("Running");
        verify(activityLogRepository, times(1)).save(activityLog);
    }

    @Test
    void createActivityLogUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.createActivityLog(1L, 10L, activityLog))
                .isInstanceOf(CustomException.class)
                .hasMessage("User not found");
    }

    @Test
    void createActivityLogWorkoutPlanNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(workoutPlanRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.createActivityLog(1L, 10L, activityLog))
                .isInstanceOf(CustomException.class)
                .hasMessage("Workout plan not found");
    }

    @Test
    void updateActivityLogSuccess() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));
        when(workoutPlanRepository.findById(10L)).thenReturn(Optional.of(workoutPlan));
        when(activityLogRepository.save(any(ActivityLog.class))).thenReturn(activityLog);

        activityLog.setActivityType("Jogging");
        ActivityLog updated = activityLogService.updateActivityLog(100L, 1L, 10L, activityLog);

        assertThat(updated.getActivityType()).isEqualTo("Jogging");
        verify(activityLogRepository, times(1)).save(activityLog);
    }

    @Test
    void updateActivityLogNotFound() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.updateActivityLog(100L, 1L, 10L, activityLog))
                .isInstanceOf(CustomException.class)
                .hasMessage("Activity log not found");
    }

    @Test
    void updateActivityLogForbidden() {
        User otherUser = new User();
        otherUser.setId(2L);
        activityLog.setUser(otherUser);

        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));

        assertThatThrownBy(() -> activityLogService.updateActivityLog(100L, 1L, 10L, activityLog))
                .isInstanceOf(CustomException.class)
                .hasMessage("Activity log does not belong to this user");
    }

    @Test
    void deleteActivityLogSuccess() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));

        activityLogService.deleteActivityLog(100L, 1L);

        verify(activityLogRepository, times(1)).delete(activityLog);
    }

    @Test
    void deleteActivityLogNotFound() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.deleteActivityLog(100L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Activity log not found");
    }

    @Test
    void deleteActivityLogForbidden() {
        User otherUser = new User();
        otherUser.setId(2L);
        activityLog.setUser(otherUser);

        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));

        assertThatThrownBy(() -> activityLogService.deleteActivityLog(100L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Activity log does not belong to this user");
    }

    @Test
    void getActivityLogSuccess() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));

        ActivityLog found = activityLogService.getActivityLog(100L, 1L);

        assertThat(found).isNotNull();
        assertThat(found.getActivityType()).isEqualTo("Running");
    }

    @Test
    void getActivityLogNotFound() {
        when(activityLogRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityLogService.getActivityLog(100L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Activity log not found");
    }

    @Test
    void getActivityLogForbidden() {
        User otherUser = new User();
        otherUser.setId(2L);
        activityLog.setUser(otherUser);

        when(activityLogRepository.findById(100L)).thenReturn(Optional.of(activityLog));

        assertThatThrownBy(() -> activityLogService.getActivityLog(100L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Activity log does not belong to this user");
    }

    @Test
    void getAllByUserSuccess() {
        when(activityLogRepository.findByUserId(1L)).thenReturn(List.of(activityLog));

        List<ActivityLog> logs = activityLogService.getAllActivityLogsByUser(1L);

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getActivityType()).isEqualTo("Running");
    }

    @Test
    void getAllByWorkoutPlanSuccess() {
        when(activityLogRepository.findByWorkoutPlanId(10L)).thenReturn(List.of(activityLog));

        List<ActivityLog> logs = activityLogService.getAllActivityLogsByWorkoutPlan(10L);

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getWorkoutPlan().getTitle()).isEqualTo("Cardio Plan");
    }
}
