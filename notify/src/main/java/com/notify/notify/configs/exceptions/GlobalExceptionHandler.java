package com.notify.notify.configs.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public void handleNoSuchElementException(NoSuchElementException ex) {
        LocalDateTime timestamp = LocalDateTime.now();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", timestamp);
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Resource Not Found");
        body.put("message", ex.getMessage());

        log.error("[Resource Not Found] Occurred at: {} - Message: {}", timestamp, ex.getMessage());
    }
}