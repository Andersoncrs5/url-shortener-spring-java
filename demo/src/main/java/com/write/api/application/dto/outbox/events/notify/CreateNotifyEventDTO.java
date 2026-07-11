package com.write.api.application.dto.outbox.events.notify;

import com.write.api.core.domain.enums.NotificationChannel;
import com.write.api.core.domain.enums.NotificationPriority;

import java.util.List;

public record CreateNotifyEventDTO(
        String message,
        List<String> recipients,
        NotificationChannel channel,
        NotificationPriority priority
) {
}