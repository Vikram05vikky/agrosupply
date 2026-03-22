package com.agrosupply.exception;

/*
 * Thrown when warehouse does not have enough stock to fulfill a request
 * HTTP Status: 400 BAD REQUEST
 *
 * Examples:
 *   - Insufficient stock for: Urea Fertilizer | Available: 20kg | Requested: 50kg
 *   - No warehouse has sufficient stock for the requested items
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}