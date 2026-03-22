package com.agrosupply.enums;

public enum DeliveryStatus {
    IN_PROGRESS,    // Delivery agent picked up and is on the way
    COMPLETED       // Delivered to farmer → triggers invoice generation
}