package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;

import java.time.LocalDateTime;

public record UpdateUrlAccessRuleDTO(

        UrlAccessRuleTypeEnum type,

        String ruleValue,

        Boolean active,

        LocalDateTime expiresAt

) {
}