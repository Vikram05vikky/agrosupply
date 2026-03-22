package com.agrosupply.enums;

public enum UserStatus {
    PENDING,    // Awaiting admin approval (for staff roles)
    ACTIVE,     // Approved and can login
    INACTIVE    // Deactivated by admin
}