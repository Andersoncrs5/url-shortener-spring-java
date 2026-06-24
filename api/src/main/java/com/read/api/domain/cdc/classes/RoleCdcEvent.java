package com.read.api.domain.cdc.classes;

import java.time.LocalDateTime;

public record RoleCdcEvent(
        Long id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
