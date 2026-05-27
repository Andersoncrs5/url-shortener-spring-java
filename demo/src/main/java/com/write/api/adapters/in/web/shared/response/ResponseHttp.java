package com.write.api.adapters.in.web.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseHttp<T>(
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

    public static <T> ResponseHttp<T> success(T data, String message) {
        return new ResponseHttp<>(
                data,
                message,
                UUID.randomUUID().toString(),
                1,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHttp<T> success(String message, String traceId) {
        return new ResponseHttp<>(
                null,
                message,
                traceId,
                1,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHttp<T> success(T data, String message, String traceId) {
        return new ResponseHttp<>(
                data,
                message,
                traceId,
                1,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHttp<T> success(T data, String message, int version) {
        return new ResponseHttp<>(
                data,
                message,
                UUID.randomUUID().toString(),
                version,
                true,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHttp<T> error(String message) {
        return new ResponseHttp<>(
                null,
                message,
                UUID.randomUUID().toString(),
                1,
                false,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHttp<T> error(String message, String traceId) {
        return new ResponseHttp<>(
                null,
                message,
                traceId,
                1,
                false,
                OffsetDateTime.now()
        );
    }

    public static <T> ResponseHttp<T> error(T value, String message, String traceId) {
        return new ResponseHttp<>(
                value,
                message,
                traceId,
                1,
                false,
                OffsetDateTime.now()
        );
    }
}