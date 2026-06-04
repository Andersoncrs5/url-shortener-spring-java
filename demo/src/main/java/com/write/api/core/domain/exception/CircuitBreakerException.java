package com.write.api.core.domain.exception;

public class CircuitBreakerException extends RuntimeException {
    private final Throwable ex;
    public CircuitBreakerException(String message, Throwable ex1) {
        super(message);
        this.ex = ex1;
    }
}
