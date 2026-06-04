package com.write.api.application.dto.url;

import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateUrlDTO(

        @Size(
                max = 2048,
                message = "Original URL must not exceed 2048 characters"
        )
        String originalUrl,

        @Size(
                max = 255,
                message = "Title must not exceed 255 characters"
        )
        String title,

        @Size(
                max = 500,
                message = "Description must not exceed 500 characters"
        )
        String description,

        @Size(
                max = 500,
                message = "Favicon URL must not exceed 500 characters"
        )
        String faviconUrl,

        @Size(
                max = 120,
                message = "Domain must not exceed 120 characters"
        )
        String domain,

        UrlStatusEnum status,

        UrlAccessTypeEnum accessType,

        @Size(
                min = 4,
                max = 255,
                message = "Password must be between 4 and 255 characters"
        )
        String password,

        @Future(
                message = "Expiration date must be in the future"
        )
        LocalDateTime expiresAt

) {
}