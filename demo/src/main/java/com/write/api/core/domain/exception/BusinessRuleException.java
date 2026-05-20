package com.write.api.core.domain.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends RuntimeException {
    private final HttpStatus status;

    public BusinessRuleException(String message) {
        super(message);

        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessRuleException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
