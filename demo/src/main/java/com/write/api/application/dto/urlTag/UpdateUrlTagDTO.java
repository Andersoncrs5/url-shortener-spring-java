package com.write.api.application.dto.urlTag;

public record UpdateUrlTagDTO(
        String name,
        String slug,
        String color,
        String description,
        Long parentId,
        boolean active
) {
}
