package com.fitness.tracker.dto.response;

import com.fitness.tracker.enums.UserRole;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {}
