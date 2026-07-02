package com.read.api.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Standard uniform HTTP response envelope wrapper used across all API endpoints.
 * It encapsulates response state, tracing diagnostics, business metadata, and payload matrices.
 *
 * @param <T> The target generic inner data payload object mapping class.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ResponseHTTP", description = "Global standard uniform api transactional envelope wrapper")
public record ResponseHTTP<T>(

        @Schema(description = "The core contextual dynamic data model payload. Omitted from structural payload if null.", nullable = true)
        T data,

        @NotBlank
        @Schema(description = "A user-friendly, descriptive semantic message or business result summary string", requiredMode = Schema.RequiredMode.REQUIRED, example = "Operation executed successfully")
        String message,

        @NotBlank
        @Schema(description = "Unique core identifier generated for microservices observability tracing and debugging logs", requiredMode = Schema.RequiredMode.REQUIRED, example = "b58f89e4-39fa-47cb-bc68-bc238ef1284d")
        String traceId,

        @NotNull
        @Schema(description = "The architectural payload data schema contract structure version layout integer", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        Integer version,

        @NotNull
        @Schema(description = "The operation boolean boolean status marker. True for business success, False for operational issues/failures", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
        boolean status,

        @NotNull
        @Schema(description = "The exact high-precision ISO-8601 server clock datetime marker string when the response frame was generated", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-07-02T15:47:28.123-03:00")
        OffsetDateTime timestamp
) {

    /**
     * Creates a success wrapper containing data payload and a random tracing identifier.
     */
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

    /**
     * Creates a void success payload response reusing an incoming pre-established structural trace key.
     */
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

    /**
     * Creates a detailed full success response using custom data payload and pre-existing tracing keys.
     */
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

    /**
     * Creates a success response allowing dynamic explicit overrides on schema API layout versions.
     */
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

    /**
     * Creates an error envelope tracking state failure and auto-generating a unique telemetry trace code.
     */
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

    /**
     * Creates an error response coupling a descriptive fail diagnostic statement with an active lookup trace context string.
     */
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

    /**
     * Creates a hybrid failure envelope appending diagnostic context object details alongside error definitions.
     */
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