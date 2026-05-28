package com.write.api.application.dto.role;

import java.time.LocalDateTime;

public record RoleDTO(
        Long id,
        String name,
        String description,
        boolean active,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}