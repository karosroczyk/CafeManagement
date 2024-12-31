package com.cafe.inventory.exception;

public class DatabaseForeignKeyValidationException extends RuntimeException {
    public DatabaseForeignKeyValidationException(String message) {
        super(message);
    }
}
