package com.fitness.tracker.configuration;

import com.fitness.tracker.entity.User;
import com.fitness.tracker.enums.UserRole;
import com.fitness.tracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class FitnessTrackerConfig {

    private Logger log = LoggerFactory.getLogger(FitnessTrackerConfig.class);

    // For the testing purpose, I have added initial admin user in the database.
    // For H2 database, we can go with data.sql, but we need to hash the password, so saving the user from here
    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByRole(UserRole.ADMIN).isEmpty()) {
                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail("admin@fitnesstracker.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                log.info("ADMIN USER Credentials: admin@fitnesstracker.com / Admin@123");
            }
        };
    }
}
