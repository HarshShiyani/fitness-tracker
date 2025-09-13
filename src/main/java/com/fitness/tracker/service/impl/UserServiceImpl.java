package com.fitness.tracker.service.impl;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.exception.CustomException;
import com.fitness.tracker.repository.UserRepository;
import com.fitness.tracker.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        log.debug("Creating user '{}' having email '{}'", user.getName(), user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("User '{}' created successfully", user.getName());
        return savedUser;
    }

    @Override
    public User updateUser(Long id, User user) {
        log.debug("Updating user having id '{}'", id);
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found"));

        log.debug("User found having id '{}' and email '{}'", existing.getId(), existing.getEmail());
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        existing.setPassword(passwordEncoder.encode(user.getPassword()));
        existing.setRole(user.getRole());
        User updatedUser = userRepository.save(existing);
        log.info("User '{}' updated successfully", updatedUser.getName());
        return updatedUser;
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user having id '{}'", id);
        if (!userRepository.existsById(id)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully");
    }

    @Override
    public User getUser(Long id) {
        log.debug("Fetching user having id '{}'", id);
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()) {
            log.error("Unable to find the user");
            throw new CustomException(HttpStatus.NOT_FOUND, "User not found");
        }
        log.info("User fetched successfully");
        return userOptional.get();
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all the users...");
        List<User> users = userRepository.findAll();
        log.info("'{}' users fetched successfully.", users.size());
        return users;
    }
}
