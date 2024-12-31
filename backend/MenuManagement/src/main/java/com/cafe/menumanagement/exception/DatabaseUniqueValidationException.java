package com.cafe.menumanagement.exception;

public class DatabaseUniqueValidationException extends RuntimeException{
    public DatabaseUniqueValidationException(String message) {
        super(message);
    }
}
