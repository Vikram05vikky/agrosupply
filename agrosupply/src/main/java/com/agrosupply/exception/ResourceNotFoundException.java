package com.agrosupply.exception;

/*
 * Thrown when a requested resource does not exist in the database
 * HTTP Status: 404 NOT FOUND
 *
 * Examples:
 *   - User not found with id: 5
 *   - Product not found with id: 3
 *   - Warehouse not found with id: 2
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}