package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;

import java.time.LocalDateTime;

public record UrlAccessRuleResponseDTO(

        Long id,

        Long urlId,

        UrlAccessRuleTypeEnum type,

        String ruleValue,

        boolean active,

        Long assignedByUserId,

        LocalDateTime expiresAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}