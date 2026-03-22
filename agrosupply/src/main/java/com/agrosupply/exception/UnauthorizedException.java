package com.agrosupply.exception;

/*
 * Thrown when login credentials are invalid or account is blocked
 * HTTP Status: 401 UNAUTHORIZED
 *
 * Examples:
 *   - Invalid email or password
 *   - Your account is pending admin approval
 *   - Your account has been deactivated
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}