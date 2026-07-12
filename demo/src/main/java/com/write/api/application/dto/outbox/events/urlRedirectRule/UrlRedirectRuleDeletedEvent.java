package com.write.api.application.dto.outbox.events.urlRedirectRule;

import com.write.api.shared.validation.snowflake.IsId;

import java.time.LocalDateTime;

public record UrlRedirectRuleDeletedEvent(
        @IsId Long id,
        @IsId Long urlId,
        LocalDateTime createdAt
) {
    public static UrlRedirectRuleDeletedEvent create(
            @IsId Long id,
            @IsId Long urlId,
            LocalDateTime createdAt
    ) {
        return new UrlRedirectRuleDeletedEvent(
                id, urlId, createdAt
        );
    }

}
