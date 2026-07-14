package com.write.api.application.dto.outbox.events.apiKey;

import com.write.api.shared.validation.snowflake.IsId;

public record ApiKeyCreatedEvent(
        @IsId Long id,
        String name,
        @IsId Long userId,
        @IsId Long ownerUserId,
        boolean active
) {
    public static ApiKeyCreatedEvent create(
            Long id,
            String name,
            Long userId,
            Long ownerUserId,
            boolean active
    ) {
        return new ApiKeyCreatedEvent(
                id, name, userId, ownerUserId, active
        );
    }
}
