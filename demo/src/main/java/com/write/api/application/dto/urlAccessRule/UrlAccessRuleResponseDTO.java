package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlAccessRuleResponseDTO(

        @IsId
        Long id,

        @IsId
        Long urlId,

        UrlAccessRuleTypeEnum type,

        String ruleValue,

        boolean active,

        @IsId
        Long assignedByUserId,

        LocalDateTime expiresAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}