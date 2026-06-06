package com.write.api.application.dto.outbox.events.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;

import java.time.LocalDateTime;

public record UrlAccessRuleDeletedEvent(
        Long id,
        Long urlId,
        Long assignedByUserId
) {

    public static UrlAccessRuleDeletedEvent create(
            Long id,
            Long urlId,
            Long assignedByUserId
    ) {
        return new UrlAccessRuleDeletedEvent(
                id,
                urlId,
                assignedByUserId
        );
    }
}