package com.write.api.application.dto.outbox.events.apiKey;

public record ApiKeyCreatedEvent(
        Long id,
        String name,
        Long userId,
        Long ownerUserId,
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
