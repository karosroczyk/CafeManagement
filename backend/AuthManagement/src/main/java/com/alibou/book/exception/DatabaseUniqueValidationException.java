package com.alibou.book.exception;

public class DatabaseUniqueValidationException extends RuntimeException{
    public DatabaseUniqueValidationException(String message) {
        super(message);
    }
}
