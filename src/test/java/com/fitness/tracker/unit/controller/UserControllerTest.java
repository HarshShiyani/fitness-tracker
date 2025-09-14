package com.fitness.tracker.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.controller.UserController;
import com.fitness.tracker.dto.request.UserRequest;
import com.fitness.tracker.dto.response.UserResponse;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.exception.GlobalExceptionHandler;
import com.fitness.tracker.mapper.UserMapper;
import com.fitness.tracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    private final UserRequest validRequest =
        new UserRequest("John Doe","john@example.com","Password@1", UserRole.USER);

    @Test
    void createUserSuccess() throws Exception {
        User userEntity = UserMapper.toEntity(validRequest);
        userEntity.setId(1L);

        when(userService.createUser(any(User.class))).thenReturn(userEntity);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.name").value("John Doe"));
    }

    @Test
    void createUserValidationFailure() throws Exception {
        UserRequest invalidRequest = new UserRequest(
                "", "john@example.com", "Password@1", UserRole.USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name is required"));
    }

    @Test
    void createUserInvalidPassword() throws Exception {
        UserRequest invalidRequest = new UserRequest(
                "John Doe", "john@example.com", "abc123", UserRole.USER);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Password must contain at least one uppercase letter, one number, and one special character"));
    }

    @Test
    void updateUserSuccess() throws Exception {
        User userEntity = UserMapper.toEntity(validRequest);
        userEntity.setId(1L);

        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(userEntity);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void updateUserValidationFailureBlankName() throws Exception {
        UserRequest invalidRequest = new UserRequest(
            "",
            "john@example.com",
            "Password@1",
            UserRole.USER
        );

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Name is required"));
    }

    @Test
    void updateUserValidationFailureInvalidEmail() throws Exception {
        UserRequest invalidRequest = new UserRequest(
            "John Doe",
            "not-an-email",
            "Password@1",
            UserRole.USER
        );

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Email must be valid"));
    }

    @Test
    void updateUserValidationFailureInvalidPassword() throws Exception {
        UserRequest invalidRequest = new UserRequest(
            "John Doe",
            "john@example.com",
            "abc123",
            UserRole.USER
        );

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("Password must contain at least one uppercase letter, one number, and one special character"));
    }


    @Test
    void deleteUserSuccess() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void getUserSuccess() throws Exception {
        User userEntity = UserMapper.toEntity(validRequest);
        userEntity.setId(1L);

        when(userService.getUser(1L)).thenReturn(userEntity);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    void getAllUsersSuccess() throws Exception {
        User userEntity = UserMapper.toEntity(validRequest);
        userEntity.setId(1L);

        when(userService.getAllUsers()).thenReturn(List.of(userEntity));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.data[0].email").value("john@example.com"));
    }
}
