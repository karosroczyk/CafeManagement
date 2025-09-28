package com.cafe.auth.exception;

public class DatabaseUniqueValidationException extends RuntimeException{
    public DatabaseUniqueValidationException(String message) {
        super(message);
    }
}
