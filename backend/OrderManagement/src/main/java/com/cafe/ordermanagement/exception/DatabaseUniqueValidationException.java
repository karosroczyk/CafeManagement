package com.cafe.ordermanagement.exception;

public class DatabaseUniqueValidationException extends RuntimeException{
    public DatabaseUniqueValidationException(String message) {
        super(message);
    }
}
