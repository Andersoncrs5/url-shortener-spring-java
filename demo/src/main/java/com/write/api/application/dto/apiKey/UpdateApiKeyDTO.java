package com.write.api.application.dto.apiKey;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateApiKeyDTO(
        @Size(
                min = 3,
                max = 100,
                message = "Api key name must be between 3 and 100 characters"
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9 _-]+$",
                message = "Api key name contains invalid characters"
        )
        String name,

        Boolean active,

        @Future(
                message = "Expiration date must be in the future"
        )
        LocalDateTime expiresAt
) {
}