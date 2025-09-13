package com.fitness.tracker.dto.response;

import java.util.List;

public record LoginResponse(
        String token,
        long expiresAt,
        List<String> roles
) {}
