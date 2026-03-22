package com.agrosupply.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agrosupply.dto.response.UserResponse;
import com.agrosupply.entity.User;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return toResponse(user);
    }

    public UserResponse updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setStatus(status);
        userRepository.save(user);

        return toResponse(user);
    }

    // Converts User entity → UserResponse DTO
    // Password is never included in the response
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .location(user.getLocation())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}