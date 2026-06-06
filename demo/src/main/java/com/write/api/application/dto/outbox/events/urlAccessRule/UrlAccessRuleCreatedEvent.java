package com.write.api.application.dto.outbox.events.urlAccessRule;

import com.write.api.application.dto.outbox.events.url.UrlCreatedEvent;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;

import java.time.LocalDateTime;
public record UrlAccessRuleCreatedEvent(
        Long id,
        Long urlId,
        Long assignedByUserId,
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