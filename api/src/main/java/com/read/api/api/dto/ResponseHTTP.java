package com.read.api.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseHTTP<T>(
        T data,

        @NotBlank
        String message,

        @NotBlank
        String traceId,

        @NotNull
        Integer version,

        @NotNull
        boolean status,

        @NotNull
        OffsetDateTime timestamp
) {

    public static <T> ResponseHTTP<T> success(T data, String message) {
        return new ResponseHTTP<>(
                data,
                message,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHTTP<T> success(String message, String traceId) {
        return new ResponseHTTP<>(
                null,
                message,
                traceId,
                1,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHTTP<T> success(T data, String message, String traceId) {
        return new ResponseHTTP<>(
                data,
                message,
                traceId,
                1,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHTTP<T> success(T data, String message, int version) {
        return new ResponseHTTP<>(
                data,
                message,
                UUID.randomUUID().toString(),
                version,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHTTP<T> error(String message) {
        return new ResponseHTTP<>(
                null,
                message,
                UUID.randomUUID().toString(),
                1,
                false,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHTTP<T> error(String message, String traceId) {
        return new ResponseHTTP<>(
                null,
                message,
                traceId,
                1,
                false,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHTTP<T> error(T value, String message, String traceId) {
        return new ResponseHTTP<>(
                value,
                message,
                traceId,
                1,
                false,
                OffsetDateTime.now()
        );
    }
}