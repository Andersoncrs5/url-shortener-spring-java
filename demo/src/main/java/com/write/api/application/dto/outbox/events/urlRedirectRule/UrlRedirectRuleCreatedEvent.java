package com.write.api.application.dto.outbox.events.urlRedirectRule;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlRedirectRuleCreatedEvent(
        @IsId Long id,
        @IsId Long urlId,
        LocalDateTime createdAt
) {
    public static UrlRedirectRuleCreatedEvent create(
            @IsId Long id,
            @IsId Long urlId,
            LocalDateTime createdAt
    ) {
        return new UrlRedirectRuleCreatedEvent(
                id, urlId, createdAt
        );
    }
}
