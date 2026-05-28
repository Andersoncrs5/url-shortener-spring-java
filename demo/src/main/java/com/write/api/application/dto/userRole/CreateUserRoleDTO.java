package com.write.api.application.dto.userRole;

import com.write.api.shared.validation.snowflake.IsId;

public record CreateUserRoleDTO(
        @IsId
        Long userId,

        @IsId
        Long roleId
) {
}