package com.write.api.application.dto.urlRedirectRule;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateUrlRedirectRuleDTO(

        @IsId
        @NotNull(message = "urlId is required")
        @Positive(message = "urlId must be greater than 0")
        Long urlId,

        @Size(max = 2, message = "countryCode must have at most 2 characters")
        String countryCode,

        @Size(max = 100, message = "region must have at most 100 characters")
        String region,

        ContinentEnum continent,

        OperatingSystemEnum os,

        BrowserEnum browser,

        @NotNull(message = "matchType is required")
        MatchTypeEnum matchType,

        @NotNull(message = "redirectUrl is required")
        @Size(max = 2048, message = "redirectUrl exceeded 2048 characters")
        String redirectUrl,

        @Positive(message = "priority must be greater than 0")
        Integer priority,

        Boolean active,

        java.time.LocalDateTime startAt,

        java.time.LocalDateTime endAt

) {
}