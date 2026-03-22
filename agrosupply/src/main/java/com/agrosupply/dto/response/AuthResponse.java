package com.agrosupply.dto.response;

import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String name;
    private String email;
    private Role role;
    private UserStatus status;
    private String message;
    // String token — will be added when security module is introduced
}