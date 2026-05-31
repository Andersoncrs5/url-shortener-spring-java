package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateUrlAccessRuleDTO(

        @IsId
        Long urlId,

        @NotNull
        UrlAccessRuleTypeEnum type,

        @NotBlank
        String ruleValue,

        LocalDateTime expiresAt

) {
}