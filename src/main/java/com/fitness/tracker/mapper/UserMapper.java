package com.fitness.tracker.mapper;

import com.fitness.tracker.dto.request.UserRequest;
import com.fitness.tracker.dto.response.UserResponse;
import com.fitness.tracker.entity.User;

public class UserMapper {

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(request.role());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
