package com.write.api.application.dto.outbox.events.urlAccessRule;

import com.write.api.shared.validation.snowflake.IsId;

public record UrlAccessRuleDeletedEvent(
        @IsId Long id,
        @IsId Long urlId,
        @IsId Long assignedByUserId
) {

    public static UrlAccessRuleDeletedEvent create(
            Long id,
            Long urlId,
            Long assignedByUserId
    ) {
        return new UrlAccessRuleDeletedEvent(
                id,
                urlId,
                assignedByUserId
        );
    }
}