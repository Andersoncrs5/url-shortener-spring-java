package com.write.api.application.dto.outbox.events.url;

import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlUpdatedEvent(
        @IsId Long id,
        String title,
        String shortCode,
        UrlStatusEnum status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UrlUpdatedEvent create(
            Long id,
            String title,
            String shortCode,
            UrlStatusEnum status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        return new UrlUpdatedEvent(id, title, shortCode, status, createdAt, updatedAt);
    }
}
