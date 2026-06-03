package com.write.api.application.dto.userRole;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UserRoleDTO(

        @IsId
        Long id,

        @IsId
        Long userId,

        @IsId
        Long roleId,

        @IsId
        Long assignedByUserId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}