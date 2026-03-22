package com.agrosupply.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.agrosupply.entity.User;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;
import com.agrosupply.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {

        // Check if admin already exists — prevents duplicate on every restart
        if (!userRepository.existsByEmail("admin@agrosupply.com")) {

            User admin = User.builder()
                    .name("Admin")
                    .email("admin@agrosupply.com")
                    .password("admin123")   // Will be BCrypt hashed when security module is added
                    .phone("0000000000")
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();

            userRepository.save(admin);

            System.out.println("✅ Default admin created — email: admin@agrosupply.com | password: admin123");
        } else {
            System.out.println("✅ Admin already exists — skipping creation");
        }
    }
}