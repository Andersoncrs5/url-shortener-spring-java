package com.read.api.domain.exceptions;

import lombok.Getter;

@Getter
public class TransientDatabaseException extends RuntimeException {
    private final Throwable throwable;

    public TransientDatabaseException(String message, Throwable throwable1) {
        super(message);
        throwable = throwable1;
    }
}
