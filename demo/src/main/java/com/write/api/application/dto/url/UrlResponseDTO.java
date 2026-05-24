package com.write.api.application.dto.url;

import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlResponseDTO(

        @IsId
        Long id,
        Long version,
        Long userId,

        String shortCode,

        String description,
        String faviconUrl,

        String originalUrl,
        String title,
        String domain,

        UrlStatusEnum status,
        UrlAccessTypeEnum accessType,

        boolean customAlias,

        LocalDateTime deletedAt,
        LocalDateTime expiresAt,
        LocalDateTime lastAccessAt,

        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
