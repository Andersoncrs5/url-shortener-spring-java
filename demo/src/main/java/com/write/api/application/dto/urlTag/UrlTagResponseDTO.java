package com.write.api.application.dto.urlTag;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlTagResponseDTO(
        @IsId
        Long id,
        @IsId
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
