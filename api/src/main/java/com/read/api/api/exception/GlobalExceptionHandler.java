package com.read.api.api.exception;

import com.read.api.api.dto.ResponseHTTP;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ConstraintViolationException.class
    )
    public ResponseEntity<ResponseHTTP<Void>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {

        String message =
                exception.getConstraintViolations()
                        .stream()
                        .findFirst()
                        .map(ConstraintViolation::getMessage)
                        .orElse("Validation error");

        log.warn(
                "Validation error: {}",
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ResponseHTTP.error(message)
                );
    }

    @ExceptionHandler(
            UsernameNotFoundException.class
    )
    public ResponseEntity<ResponseHTTP<String>> handleNotFound(
            UsernameNotFoundException exception
    ) {

        log.warn(
                "User not found: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ResponseHTTP.error(
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            DisabledException.class
    )
    public ResponseEntity<ResponseHTTP<String>> handleDisabled(
            DisabledException exception
    ) {

        log.warn(
                "Disabled account access attempt: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ResponseHTTP.error(
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            LockedException.class
    )
    public ResponseEntity<ResponseHTTP<String>> handleLocked(
            LockedException exception
    ) {

        log.warn(
                "Locked account access attempt: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(
                        ResponseHTTP.error(
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<ResponseHTTP<String>> handleBadRequest(
            IllegalArgumentException exception
    ) {

        log.warn(
                "Bad request: {}",
                exception.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(
                        ResponseHTTP.error(
                                exception.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ResponseHTTP<String>> handleGeneric(
            Exception exception
    ) {

        log.error(
                "Unexpected internal error",
                exception
        );

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        ResponseHTTP.error(
                                "Internal server error"
                        )
                );
    }
}