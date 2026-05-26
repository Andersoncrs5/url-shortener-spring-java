package com.write.api.infrastructure.exception;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.shared.validation.ValidationErrorItem;
import com.write.api.application.shared.validation.ValidationErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseHttp<Void>> handleConstraintViolation(
            ConstraintViolationException ex
    ) {

        String message = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("Validation error");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ResponseHttp.error(
                                message,
                                null
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex
    ) {

        Map<String, List<ValidationErrorItem>> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .collect(Collectors.groupingBy(
                                FieldError::getField,
                                Collectors.mapping(
                                        fieldError -> new ValidationErrorItem(
                                                fieldError.getCode(),
                                                fieldError.getDefaultMessage()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        false,
                        "Validation error",
                        errors,
                        null
                );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

}