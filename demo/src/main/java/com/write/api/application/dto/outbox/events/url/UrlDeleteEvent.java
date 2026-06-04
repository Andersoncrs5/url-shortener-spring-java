package com.write.api.application.dto.outbox.events.url;

public record UrlDeleteEvent(
        Long id,
        String title,
        String shortCode
) {
    public static UrlDeleteEvent create(Long id, String title, String shortCode) {
        return new UrlDeleteEvent(id, title, shortCode);
    }
}
