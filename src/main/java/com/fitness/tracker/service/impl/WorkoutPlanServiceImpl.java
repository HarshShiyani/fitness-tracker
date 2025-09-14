package com.fitness.tracker.service.impl;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.entity.WorkoutPlan;
import com.fitness.tracker.exception.CustomException;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.repository.WorkoutPlanRepository;
import com.fitness.tracker.service.WorkoutPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    private final Logger log = LoggerFactory.getLogger(WorkoutPlanServiceImpl.class);

    private final WorkoutPlanRepository workoutPlanRepository;
    private final UserRepository userRepository;

    public WorkoutPlanServiceImpl(WorkoutPlanRepository workoutPlanRepository, UserRepository userRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WorkoutPlan createWorkoutPlan(Long userId, WorkoutPlan workoutPlan) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        workoutPlan.setUser(user);
        WorkoutPlan savedPlan = workoutPlanRepository.save(workoutPlan);
        log.info("Workout plan '{}' created for user '{}'", savedPlan.getTitle(), user.getEmail());
        return savedPlan;
    }

    @Override
    public WorkoutPlan updateWorkoutPlan(Long id, Long userId, WorkoutPlan workoutPlan) {
        WorkoutPlan existing = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Workout plan not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Not authorized to update this workout plan");
        }

        existing.setTitle(workoutPlan.getTitle());
        existing.setDescription(workoutPlan.getDescription());
        existing.setDuration(workoutPlan.getDuration());

        WorkoutPlan updated = workoutPlanRepository.save(existing);
        log.info("Workout plan '{}' updated for user '{}'", updated.getTitle(), existing.getUser().getEmail());
        return updated;
    }

    @Override
    public void deleteWorkoutPlan(Long id, Long userId) {
        WorkoutPlan existing = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Workout plan not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Not authorized to delete this workout plan");
        }

        workoutPlanRepository.delete(existing);
        log.info("Workout plan '{}' deleted for user '{}'", existing.getTitle(), existing.getUser().getEmail());
    }

    @Override
    public WorkoutPlan getWorkoutPlan(Long id, Long userId) {
        WorkoutPlan plan = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Workout plan not found"));

        if (!plan.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "Not authorized to view this workout plan");
        }

        return plan;
    }

    @Override
    public List<WorkoutPlan> getAllWorkoutPlans(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getWorkoutPlans();
    }
}
