package com.notify.notify.modules.notifications.entities;

import com.notify.notify.globals.enums.NotificationChannel;
import com.notify.notify.globals.enums.NotificationStatus;
import com.notify.notify.utils.base.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends BaseEntity {

    private String recipient;
    private NotificationChannel channel;
    private String subject;
    private String body;
    private NotificationStatus status;
    private String templateName;
    private Integer retryCount;
    private String providerMessageId;
}