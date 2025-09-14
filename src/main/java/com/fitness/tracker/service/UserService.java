package com.fitness.tracker.service;

import com.fitness.tracker.entity.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User updateUser(Long id, User updatedUser);

    void deleteUser(Long id);

    User getUser(Long id);

    List<User> getAllUsers();
}
