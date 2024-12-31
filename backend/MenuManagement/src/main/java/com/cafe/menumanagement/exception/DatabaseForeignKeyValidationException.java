package com.cafe.menumanagement.exception;

public class DatabaseForeignKeyValidationException extends RuntimeException {
    public DatabaseForeignKeyValidationException(String message) {
        super(message);
    }
}
