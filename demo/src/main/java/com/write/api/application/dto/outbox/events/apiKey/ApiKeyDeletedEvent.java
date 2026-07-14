package com.write.api.application.dto.outbox.events.apiKey;

import com.write.api.shared.validation.snowflake.IsId;

public record ApiKeyDeletedEvent(
        @IsId Long id,
        String name,
        @IsId Long userId,
        @IsId Long ownerUserId,
        boolean active
) {
    public static ApiKeyDeletedEvent create(
            Long id,
            String name,
            Long userId,
            Long ownerUserId,
            boolean active
    ) {
        return new ApiKeyDeletedEvent(
                id, name, userId, ownerUserId, active
        );
    }
}
