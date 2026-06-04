package com.write.api.application.dto.role;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record RoleDTO(
        @IsId
        Long id,
        String name,
        String description,
        boolean active,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}