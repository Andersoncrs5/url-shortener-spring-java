package com.write.api.application.dto.outbox.events.url;

import com.write.api.shared.validation.snowflake.IsId;

public record UrlDeleteEvent(
        @IsId Long id,
        String title,
        String shortCode
) {
    public static UrlDeleteEvent create(Long id, String title, String shortCode) {
        return new UrlDeleteEvent(id, title, shortCode);
    }
}
