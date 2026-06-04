package com.write.api.application.dto.outbox.events.url;

import com.write.api.core.domain.enums.UrlStatusEnum;

import java.time.LocalDateTime;

public record UrlCreatedEvent(
        Long id,
        String title,
        String shortCode,
        UrlStatusEnum status,
        LocalDateTime createdAt
) {
    public static UrlCreatedEvent create(Long id, String title, String shortCode, UrlStatusEnum status, LocalDateTime createdAt) {
        return new UrlCreatedEvent(id, title, shortCode, status, createdAt);
    }
}
