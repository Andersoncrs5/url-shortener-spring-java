package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateUrlAccessRuleDTO(

        UrlAccessRuleTypeEnum type,

        @Size(
                max = 250,
                message = "Rule value must not exceed 250 characters"
        )
        String ruleValue,

        Boolean active,

        @Future(message = "Expiration date must be in the future")
        LocalDateTime expiresAt

) {
}