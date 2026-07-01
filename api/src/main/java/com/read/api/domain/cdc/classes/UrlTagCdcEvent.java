package com.read.api.domain.cdc.classes;

import com.read.api.domain.cdc.BaseCdcEvent;

import java.time.LocalDateTime;

public record UrlTagCdcEvent(
        Long id,
        Long userId,
        String name,
        String slug,
        String color,
        String description,
        Long parentId,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseCdcEvent {
}
