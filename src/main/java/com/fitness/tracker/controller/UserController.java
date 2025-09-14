package com.fitness.tracker.controller;

import com.fitness.tracker.dto.BaseResponse;
import com.fitness.tracker.dto.request.UserRequest;
import com.fitness.tracker.dto.response.UserResponse;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.mapper.UserMapper;
import com.fitness.tracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Create, Get, Update & Delete the users")
@SecurityRequirement(name = "X-USER-ID")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user", description = "Creates a user with name, email, password, and role")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @PostMapping
    public ResponseEntity<BaseResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        User user = userService.createUser(UserMapper.toEntity(request));
        return ResponseEntity.ok(new BaseResponse<>("User created successfully", UserMapper.toResponse(user)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @Operation(summary = "Update an existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request
    ) {
        User updated = userService.updateUser(id, UserMapper.toEntity(request));
        return ResponseEntity.ok(new BaseResponse<>("User updated successfully", UserMapper.toResponse(updated)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new BaseResponse<>("User deleted successfully"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'GUEST')")
    @Operation(summary = "Get a user by ID")
    @ApiResponse(responseCode = "200", description = "User fetched successfully")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponse>> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(new BaseResponse<>("User fetched successfully", UserMapper.toResponse(user)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users fetched successfully")
    @GetMapping
    public ResponseEntity<BaseResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
        return ResponseEntity.ok(new BaseResponse<>("Users fetched successfully", users));
    }
}
