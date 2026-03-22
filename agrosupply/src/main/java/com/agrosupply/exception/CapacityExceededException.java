package com.agrosupply.exception;

/*
 * Thrown when adding stock would exceed warehouse capacity
 * HTTP Status: 400 BAD REQUEST
 *
 * Examples:
 *   - Warehouse kg capacity exceeded! Max: 500000kg | Current: 498000kg | Incoming: 5000kg
 */
public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(String message) {
        super(message);
    }
}