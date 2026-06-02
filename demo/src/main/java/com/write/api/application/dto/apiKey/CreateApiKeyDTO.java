package com.write.api.application.dto.apiKey;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateApiKeyDTO(

        @NotBlank
        String name,

        @Future
        LocalDateTime expiresAt,

        boolean active
) {
}