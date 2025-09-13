package com.fitness.tracker.repository;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.enums.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRole(UserRole role);
    Optional<User> findByEmail(String email);
}
