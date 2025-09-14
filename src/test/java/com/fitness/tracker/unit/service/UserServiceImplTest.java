package com.fitness.tracker.unit.service;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.exception.CustomException;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.service.impl.UserServiceImpl;
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

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("Password@1");
        user.setRole(UserRole.USER);
    }

    @Test
    void createUserSuccess() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.createUser(user);

        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        user.setName("Updated Name");
        User updated = userService.updateUser(1L, user);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, user))
                .isInstanceOf(CustomException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUser(1L);

        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("john@example.com");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(1L))
                .isInstanceOf(CustomException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void getAllUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("john@example.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> users = userService.getAllUsers();

        assertThat(users).isEmpty();
        verify(userRepository, times(1)).findAll();
    }
}
