package com.write.api.application.dto.apiKey;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record ApiKeyDTO(

        @IsId
        Long id,

        String name,

        boolean active,

        LocalDateTime lastUsedAt,

        LocalDateTime expiresAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}