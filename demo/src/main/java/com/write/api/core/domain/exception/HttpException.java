package com.write.api.core.domain.exception;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
    private final HttpStatus status;

    public HttpException(String message) {
        super(message);

        this.status = HttpStatus.BAD_REQUEST;
    }

    public HttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
