package com.cafe.auth.exception;

public class DatabaseForeignKeyValidationException extends RuntimeException {
    public DatabaseForeignKeyValidationException(String message) {
        super(message);
    }
}
