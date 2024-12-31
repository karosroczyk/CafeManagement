package com.cafe.ordermanagement.exception;

public class DatabaseForeignKeyValidationException extends RuntimeException {
    public DatabaseForeignKeyValidationException(String message) {
        super(message);
    }
}
