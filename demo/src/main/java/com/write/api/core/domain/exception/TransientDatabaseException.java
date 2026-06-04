package com.write.api.core.domain.exception;

public class TransientDatabaseException
        extends RuntimeException {

    public TransientDatabaseException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}