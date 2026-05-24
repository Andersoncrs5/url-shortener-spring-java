package com.write.api.application.dto.urlTag;

import java.time.LocalDateTime;

public record UrlTagResponseDTO(
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
) {
}
