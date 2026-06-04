package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateUrlAccessRuleDTO(

        @IsId(message = "Url id is invalid")
        Long urlId,

        @NotNull(message = "Rule type is required")
        UrlAccessRuleTypeEnum type,

        @NotBlank(message = "Rule value is required")
        @Size(
                max = 250,
                message = "Rule value must not exceed 250 characters"
        )
        String ruleValue,

        @Future(message = "Expiration date must be in the future")
        LocalDateTime expiresAt

) {
}