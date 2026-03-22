package com.agrosupply.enums;

public enum OrderStatus {
    PENDING,    // Order placed, waiting for delivery
    DELIVERED,  // Goods received → triggers inventory update
    CANCELLED   // Order cancelled by procurement officer
}