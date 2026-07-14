package com.write.api.application.dto.outbox.events.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlAccessRuleCreatedEvent(
        @IsId Long id,
        @IsId Long urlId,
        @IsId Long assignedByUserId,
        String ruleValue,
        UrlAccessRuleTypeEnum type,
        LocalDateTime createdAt
) {

    public static UrlAccessRuleCreatedEvent create(
            Long id,
            Long urlId,
            Long assignedByUserId,
            String ruleValue,
            UrlAccessRuleTypeEnum type,
            LocalDateTime createdAt
    ) {
        return new UrlAccessRuleCreatedEvent(
                id,
                urlId,
                assignedByUserId,
                ruleValue,
                type,
                createdAt
        );
    }
}