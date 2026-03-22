package com.agrosupply.enums;

public enum RequestStatus {
    PENDING,    // Farmer placed request, waiting for warehouse operator review
    ALLOCATED,  // Stock reserved from warehouse, inventory reduced
    SCHEDULED,  // Delivery agent assigned, ready for pickup
    DELIVERED,  // Delivery completed, invoice auto-generated
    CANCELLED   // Request cancelled
}