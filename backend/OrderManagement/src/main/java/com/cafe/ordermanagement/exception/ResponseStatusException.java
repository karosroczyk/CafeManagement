package com.cafe.ordermanagement.exception;

public class ResponseStatusException extends RuntimeException{
    public ResponseStatusException(String message) {
        super(message);
    }
}
