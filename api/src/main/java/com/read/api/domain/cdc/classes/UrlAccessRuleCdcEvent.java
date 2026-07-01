package com.read.api.domain.cdc.classes;

import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;

import java.time.LocalDateTime;

public record UrlAccessRuleCdcEvent(
        Long id,
        Long urlId,
        UrlAccessRuleTypeEnum type,
        String ruleValue,
        boolean active,
        Long assignedByUserId,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseCdcEvent {
}
