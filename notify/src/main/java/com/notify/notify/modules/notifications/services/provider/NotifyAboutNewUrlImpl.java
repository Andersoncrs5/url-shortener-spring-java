package com.notify.notify.modules.notifications.services.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.globals.classes.outbox.OutboxCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.enums.NotificationChannel;
import com.notify.notify.modules.email.services.base.EmailService;
import com.notify.notify.modules.notifications.entities.NotificationEntity;
import com.notify.notify.utils.annotations.UseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotifyAboutNewUrlImpl {

    ObjectMapper mapper;
    EmailService emailService;

    public Result<Void> execute(OutboxCdcEvent outboxEvent) {
        NotificationEntity noti = new NotificationEntity();
        noti.setRecipient(null);
        noti.setChannel(NotificationChannel.EMAIL);
        noti.setSubject("Url created with success!");

        return Result.success();
    }

}
