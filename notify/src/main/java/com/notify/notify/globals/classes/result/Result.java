package com.notify.notify.globals.classes.result;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
public class Result<T> {

    private final T value;
    private final List<String> errors;
    private final HttpStatus statusCode;

    private Result(T value, List<String> errors, HttpStatus statusCode) {
        this.value = value;
        this.errors = errors == null ? Collections.emptyList() : List.copyOf(errors);
        this.statusCode = statusCode;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    public Optional<String> getFirstError() {
        return errors.stream().findFirst();
    }

    public Optional<String> getMessage() {
        return errors.stream().findFirst();
    }

    public static <T> Result<T> success() {
        return new Result<>(null, List.of(), HttpStatus.OK);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, List.of(), HttpStatus.OK);
    }

    public static <T> Result<T> success(T value, HttpStatus status) {
        return new Result<>(value, List.of(), status);
    }

    public static <T> Result<T> success(HttpStatus status) {
        return new Result<>(null, List.of(), status);
    }

    public static <T> Result<T> failure(Result<T> result) {
        return new Result<>(result.value, result.errors, result.statusCode);
    }

    public static <T> Result<T> failure(String error, HttpStatus status) {
        return new Result<>(null, List.of(error), status);
    }

    public static <T> Result<T> failure(List<String> errors, HttpStatus status) {
        return new Result<>(null, errors, status);
    }
}