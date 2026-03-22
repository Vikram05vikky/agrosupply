package com.agrosupply.enums;

public enum StockMovementStatus {
    PENDING,    // Transfer requested, waiting for destination warehouse operator approval
    APPROVED,   // Approved — inventory updated (deducted from source, added to destination)
    REJECTED    // Rejected — no inventory change
}