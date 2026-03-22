package com.agrosupply.exception;

/*
 * Thrown when request data is invalid or business rule is violated
 * HTTP Status: 400 BAD REQUEST
 *
 * Examples:
 *   - Email already registered
 *   - Only PENDING orders can be cancelled
 *   - Source and destination warehouse cannot be the same
 *   - Invoice is already paid
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}