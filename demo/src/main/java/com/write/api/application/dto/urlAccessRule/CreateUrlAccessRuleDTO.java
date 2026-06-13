package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.shared.validation.snowflake.IsId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateUrlAccessRuleDTO(

        @IsId(message = "Url id is invalid")
        @Schema(
                description = "Target URL identifier",
                example = "92183746518273645"
        )
        Long urlId,

        @NotNull(message = "Rule type is required")
        @Schema(
                description = """
                        Access rule type.

                        Supported values:
                        - MAX_CLICKS
                        - RATE_LIMIT
                        - EXPIRES_AT
                        - COUNTRY_ALLOW
                        - COUNTRY_BLOCK
                        - IP_ALLOW
                        - IP_BLOCK
                        - USER_AGENT_BLOCK
                        """
        )
        UrlAccessRuleTypeEnum type,

        @NotBlank(message = "Rule value is required")
        @Size(
                max = 250,
                message = "Rule value must not exceed 250 characters"
        )
        @Schema(
                description = """
                        Rule value.

                        Examples:
                        - MAX_CLICKS -> 100
                        - RATE_LIMIT -> 50
                        - COUNTRY_ALLOW -> BR
                        - COUNTRY_BLOCK -> US
                        - IP_ALLOW -> 192.168.1.10
                        - IP_BLOCK -> 8.8.8.8
                        - USER_AGENT_BLOCK -> curl
                        """,
                example = "BR"
        )
        String ruleValue,

        @Schema(
                description = """
                        Expiration date.

                        Required only when type is EXPIRES_AT.
                        Must be a future date.
                        """,
                example = "2027-01-01T00:00:00"
        )
        @Future(message = "Expiration date must be in the future")
        LocalDateTime expiresAt

) {
}