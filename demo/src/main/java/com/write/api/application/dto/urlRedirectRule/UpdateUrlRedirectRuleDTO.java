package com.write.api.application.dto.urlRedirectRule;

import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateUrlRedirectRuleDTO(

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

        MatchTypeEnum matchType,

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

        LocalDateTime startAt,

        LocalDateTime endAt

) {
}