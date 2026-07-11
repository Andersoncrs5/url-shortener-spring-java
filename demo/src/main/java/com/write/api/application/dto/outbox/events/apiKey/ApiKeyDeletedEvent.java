package com.write.api.application.dto.outbox.events.apiKey;

public record ApiKeyDeletedEvent(
        Long id,
        String name,
        Long userId,
        Long ownerUserId,
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
