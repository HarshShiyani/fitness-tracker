package com.fitness.tracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record WorkoutPlanRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        int duration
) {}
