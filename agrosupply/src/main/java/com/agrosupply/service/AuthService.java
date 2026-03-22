package com.agrosupply.service;

import org.springframework.stereotype.Service;

import com.agrosupply.dto.request.LoginRequest;
import com.agrosupply.dto.request.RegisterRequest;
import com.agrosupply.dto.response.AuthResponse;
import com.agrosupply.entity.User;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.UnauthorizedException;
import com.agrosupply.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            // throw new RuntimeException("Email already registered");
            throw new BadRequestException("Email already registered");
        }

        // Farmers are auto-approved; staff roles wait for admin approval
        UserStatus initialStatus = (request.getRole() == Role.FARMER)
                ? UserStatus.ACTIVE
                : UserStatus.PENDING;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .location(request.getLocation())
                .role(request.getRole())
                .status(initialStatus)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .message(initialStatus == UserStatus.ACTIVE
                        ? "Registration successful"
                        : "Registration successful. Awaiting admin approval.")
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getStatus() == UserStatus.PENDING) {
            // throw new RuntimeException("Your account is pending admin approval");
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            // throw new RuntimeException("Your account has been deactivated. Contact admin.");
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!request.getPassword().equals(user.getPassword())) {
            // throw new RuntimeException("Invalid email or password");
            throw new UnauthorizedException("Invalid email or password");
        }

        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .message("Login successful")
                .build();
    }
}