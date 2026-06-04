package com.write.api.application.dto.urlRedirectRule;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record CreateUrlRedirectRuleDTO(

        @IsId(message = "Url id is invalid")
        Long urlId,

        @Size(
                min = 2,
                max = 2,
                message = "Country code must contain exactly 2 characters"
        )
        String countryCode,

        @Size(
                max = 100,
                message = "Region must not exceed 100 characters"
        )
        String region,

        ContinentEnum continent,

        OperatingSystemEnum os,

        BrowserEnum browser,

        @NotNull(message = "Match type is required")
        MatchTypeEnum matchType,

        @NotBlank(message = "Redirect URL is required")
        @Size(
                max = 2048,
                message = "Redirect URL must not exceed 2048 characters"
        )
        String redirectUrl,

        @Min(
                value = 0,
                message = "Priority must be greater than or equal to 0"
        )
        Integer priority,

        Boolean active,

        @FutureOrPresent(
                message = "Start date must be in the present or future"
        )
        LocalDateTime startAt,

        LocalDateTime endAt

) {
}