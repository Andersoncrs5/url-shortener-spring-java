package com.read.api.domain.exceptions;

public class TransientDatabaseException extends RuntimeException {
    public TransientDatabaseException(String message) {
        super(message);
    }
}
