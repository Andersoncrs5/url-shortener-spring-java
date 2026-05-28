package com.write.api.application.dto.userRole;

import java.time.LocalDateTime;

public record UserRoleDTO(

        Long id,

        Long userId,

        Long roleId,

        Long assignedByUserId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}