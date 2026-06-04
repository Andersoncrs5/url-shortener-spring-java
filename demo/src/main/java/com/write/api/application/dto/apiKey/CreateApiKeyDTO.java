package com.write.api.application.dto.apiKey;

import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateApiKeyDTO(

        @NotBlank(message = "Api key name is required")
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

        @Future(
                message = "Expiration date must be in the future"
        )
        LocalDateTime expiresAt,

        boolean active,

        @IsId(
                message = "Owner user id is invalid"
        )
        Long ownerUserId
) {
}