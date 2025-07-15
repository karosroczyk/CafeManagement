package com.alibou.book.exception;

public class DatabaseForeignKeyValidationException extends RuntimeException {
    public DatabaseForeignKeyValidationException(String message) {
        super(message);
    }
}
