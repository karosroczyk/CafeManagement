package com.cafe.inventory.exception;

public class DatabaseUniqueValidationException extends RuntimeException{
    public DatabaseUniqueValidationException(String message) {
        super(message);
    }
}
