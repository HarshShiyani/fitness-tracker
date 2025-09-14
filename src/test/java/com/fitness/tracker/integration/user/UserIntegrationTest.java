package com.fitness.tracker.integration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.tracker.dto.request.UserRequest;
import com.fitness.tracker.entity.User;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void initAdmin() {
        userRepository.deleteAll();
        User admin = new User();
        admin.setName("System Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword("Admin@123");
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
    }


    @Test
    void createUserSuccess() throws Exception {
        UserRequest request = new UserRequest(
                "John Doe",
                "john@example.com",
                "Password@1",
                UserRole.USER
        );

        mockMvc.perform(post("/api/users")
                .header("X-USER-ID", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));

        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    void createUserValidationFailure() throws Exception {
        UserRequest invalid = new UserRequest(
                "", "not-an-email", "abc123", UserRole.USER
        );

        mockMvc.perform(post("/api/users")
                        .header("X-USER-ID", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getUserSuccess() throws Exception {
        User saved = userRepository.save(new User(
                null, "Alice", "alice@example.com", "Password@1", UserRole.ADMIN, null,null, null
        ));

        mockMvc.perform(get("/api/users/" + saved.getId())
                    .header("X-USER-ID", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Alice"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void getUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999")
                    .header("X-USER-ID", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void updateUserSuccess() throws Exception {
        User saved = userRepository.save(new User(
                null, "Bob", "bob@example.com", "Password@1", UserRole.USER, null, null, null
        ));

        UserRequest updateRequest = new UserRequest(
                "Bob Updated", "bob.new@example.com", "Password@2", UserRole.ADMIN
        );

        mockMvc.perform(put("/api/users/" + saved.getId())
                        .header("X-USER-ID", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Bob Updated"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        assertThat(userRepository.findById(saved.getId()))
                .get()
                .extracting(User::getName)
                .isEqualTo("Bob Updated");
    }

    @Test
    void updateUserNotFound() throws Exception {
        UserRequest updateRequest = new UserRequest(
                "Ghost", "ghost@example.com", "Password@1", UserRole.USER
        );

        mockMvc.perform(put("/api/users/12345")
                        .header("X-USER-ID", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void deleteUserSuccess() throws Exception {
        User saved = userRepository.save(new User(
                null, "Charlie", "charlie@example.com", "Password@1", UserRole.USER,null, null, null
        ));

        mockMvc.perform(delete("/api/users/" + saved.getId())
                    .header("X-USER-ID", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        assertThat(userRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void deleteUserNotFound() throws Exception {
        mockMvc.perform(delete("/api/users/99999")
                    .header("X-USER-ID", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}
