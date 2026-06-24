package com.read.api.domain.cdc.classes;

import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;

import java.time.LocalDateTime;

public record UrlCdcEvent(
        Long id,

        Long userId,
        String shortCode,
        String description,
        String faviconUrl,
        String originalUrl,
        String title,
        String domain,
        UrlStatusEnum status,
        UrlAccessTypeEnum accessType,
        String passwordHash,
        boolean customAlias,
        LocalDateTime deletedAt,
        LocalDateTime expiresAt,
        LocalDateTime lastAccessAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
