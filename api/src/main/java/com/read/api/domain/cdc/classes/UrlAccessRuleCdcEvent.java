package com.read.api.domain.cdc.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.infrastructure.config.jackson.Boolean01Deserializer;
import java.time.LocalDateTime;

public record UrlAccessRuleCdcEvent(
        Long id,
        Long urlId,
        UrlAccessRuleTypeEnum type,
        String ruleValue,
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean active,
        Long assignedByUserId,
        LocalDateTime expiresAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseCdcEvent {
        public UrlAccessRuleCdcEvent {
                if (ruleValue == null) ruleValue = "";
                if (active == null) active = false;
        }
}