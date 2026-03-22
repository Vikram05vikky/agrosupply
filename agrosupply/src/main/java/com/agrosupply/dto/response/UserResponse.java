package com.agrosupply.dto.response;

import java.time.LocalDateTime;

import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}