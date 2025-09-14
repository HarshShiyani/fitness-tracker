package com.fitness.tracker.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActivityLogRequest(

        @NotBlank(message = "Activity type is required")
        @Size(min = 3, max = 50, message = "Activity type must be between 3 and 50 characters")
        String activityType,

        @Min(value = 1, message = "Calories burned must be greater than 0")
        int caloriesBurned,

        @Min(value = 1, message = "Duration must be greater than 0")
        int duration
) {}
