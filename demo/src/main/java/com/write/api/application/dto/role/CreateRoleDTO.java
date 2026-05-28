package com.write.api.application.dto.role;

public record CreateRoleDTO(
        String name,
        String description,
        boolean active
) {
}
