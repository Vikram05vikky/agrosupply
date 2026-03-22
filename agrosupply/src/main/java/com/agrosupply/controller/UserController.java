package com.agrosupply.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosupply.dto.response.UserResponse;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;
import com.agrosupply.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(@RequestParam UserStatus status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    @GetMapping("/role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@RequestParam Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id,
                                                          @RequestParam UserStatus status) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status));
    }
}