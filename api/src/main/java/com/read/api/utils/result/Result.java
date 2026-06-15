package com.read.api.utils.result;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
    private final T value;
    private final List<String> errors;
    private final int statusCode;
    private final boolean isSuccess;

    private Result(T value, List<String> errors, int statusCode, boolean isSuccess) {
        this.value = value;
        this.errors = errors != null ? errors : new ArrayList<>();
        this.statusCode = statusCode;
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return errors.stream()
                .findFirst()
                .orElse(null);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(value, null, 200, true);
    }

    public static <T> Result<T> success() {
        return new Result<>(null, null, 200, true);
    }

    public static <T> Result<T> success(T value, int statusCode) {
        return new Result<>(value, null, statusCode, true);
    }

    public static <T> Result<T> success(int statusCode) {
        return new Result<>(null, null, statusCode, true);
    }

    public static <T> Result<T> failure(String error, int statusCode) {
        return new Result<>(null, List.of(error), statusCode, false);
    }

    public static <T> Result<T> failure(String error, int statusCode, T body) {
        return new Result<>(body, List.of(error), statusCode, false);
    }

    public static <T> Result<T> failure(List<String> errors, int statusCode) {
        return new Result<>(null, errors, statusCode, false);
    }

    public static <T> Result<T> failure(int statusCode, String... errors) {
        return new Result<>(null, List.of(errors), statusCode, false);
    }

    public boolean isSuccess() { return isSuccess; }
    public boolean isFailure() { return !isSuccess; }
    public T getValue() { return value; }
    public List<String> getErrors() { return errors; }
    public int getStatusCode() { return statusCode; }
}