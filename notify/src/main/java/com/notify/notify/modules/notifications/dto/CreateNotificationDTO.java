package com.notify.notify.modules.notifications.dto;

import com.notify.notify.globals.enums.NotificationChannel;

public record CreateNotificationDTO(
        String recipient,
        NotificationChannel channel,
        String subject,
        String body
) {
}
