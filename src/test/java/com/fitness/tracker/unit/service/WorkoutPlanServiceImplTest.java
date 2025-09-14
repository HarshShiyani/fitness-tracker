package com.fitness.tracker.unit.service;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.exception.CustomException;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.WorkoutPlanRepository;
import com.fitness.tracker.service.impl.WorkoutPlanServiceImpl;
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

class WorkoutPlanServiceImplTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutPlanServiceImpl workoutPlanService;

    private User user;
    private WorkoutPlan workoutPlan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password@1");
        user.setRole(UserRole.USER);

        workoutPlan = new WorkoutPlan();
        workoutPlan.setId(1L);
        workoutPlan.setTitle("Plan A");
        workoutPlan.setDescription("Strength Training");
        workoutPlan.setDuration(30);
        workoutPlan.setUser(user);
    }

    @Test
    void createWorkoutPlanSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(workoutPlanRepository.save(any(WorkoutPlan.class))).thenReturn(workoutPlan);

        WorkoutPlan saved = workoutPlanService.createWorkoutPlan(1L, workoutPlan);

        assertThat(saved.getTitle()).isEqualTo("Plan A");
        verify(userRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, times(1)).save(workoutPlan);
    }

    @Test
    void createWorkoutPlanUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutPlanService.createWorkoutPlan(1L, workoutPlan))
                .isInstanceOf(CustomException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, never()).save(any(WorkoutPlan.class));
    }

    @Test
    void updateWorkoutPlanSuccess() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));
        when(workoutPlanRepository.save(any(WorkoutPlan.class))).thenReturn(workoutPlan);

        workoutPlan.setTitle("Updated Plan");
        WorkoutPlan updated = workoutPlanService.updateWorkoutPlan(1L, 1L, workoutPlan);

        assertThat(updated.getTitle()).isEqualTo("Updated Plan");
        verify(workoutPlanRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, times(1)).save(workoutPlan);
    }

    @Test
    void updateWorkoutPlanNotFound() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutPlanService.updateWorkoutPlan(1L, 1L, workoutPlan))
                .isInstanceOf(CustomException.class)
                .hasMessage("Workout plan not found");

        verify(workoutPlanRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, never()).save(any(WorkoutPlan.class));
    }

    @Test
    void updateWorkoutPlanForbidden() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        workoutPlan.setUser(anotherUser);

        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));

        assertThatThrownBy(() -> workoutPlanService.updateWorkoutPlan(1L, 1L, workoutPlan))
                .isInstanceOf(CustomException.class)
                .hasMessage("Not authorized to update this workout plan");

        verify(workoutPlanRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, never()).save(any(WorkoutPlan.class));
    }

    @Test
    void deleteWorkoutPlanSuccess() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));

        workoutPlanService.deleteWorkoutPlan(1L, 1L);

        verify(workoutPlanRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, times(1)).delete(workoutPlan);
    }

    @Test
    void deleteWorkoutPlanNotFound() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutPlanService.deleteWorkoutPlan(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Workout plan not found");

        verify(workoutPlanRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, never()).delete(any(WorkoutPlan.class));
    }

    @Test
    void deleteWorkoutPlanForbidden() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        workoutPlan.setUser(anotherUser);

        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));

        assertThatThrownBy(() -> workoutPlanService.deleteWorkoutPlan(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Not authorized to delete this workout plan");

        verify(workoutPlanRepository, times(1)).findById(1L);
        verify(workoutPlanRepository, never()).delete(any(WorkoutPlan.class));
    }

    @Test
    void getWorkoutPlanSuccess() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));

        WorkoutPlan found = workoutPlanService.getWorkoutPlan(1L, 1L);

        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Plan A");
        verify(workoutPlanRepository, times(1)).findById(1L);
    }

    @Test
    void getWorkoutPlanNotFound() {
        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutPlanService.getWorkoutPlan(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Workout plan not found");

        verify(workoutPlanRepository, times(1)).findById(1L);
    }

    @Test
    void getWorkoutPlanForbidden() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        workoutPlan.setUser(anotherUser);

        when(workoutPlanRepository.findById(1L)).thenReturn(Optional.of(workoutPlan));

        assertThatThrownBy(() -> workoutPlanService.getWorkoutPlan(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("Not authorized to view this workout plan");

        verify(workoutPlanRepository, times(1)).findById(1L);
    }

    @Test
    void getAllWorkoutPlansSuccess() {
        user.setWorkoutPlans(List.of(workoutPlan));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<WorkoutPlan> plans = workoutPlanService.getAllWorkoutPlans(1L);

        assertThat(plans).hasSize(1);
        assertThat(plans.get(0).getTitle()).isEqualTo("Plan A");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllWorkoutPlansUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workoutPlanService.getAllWorkoutPlans(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(1L);
    }
}
