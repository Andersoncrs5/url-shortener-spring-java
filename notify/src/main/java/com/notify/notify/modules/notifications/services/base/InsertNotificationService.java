package com.notify.notify.modules.notifications.services.base;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.notifications.entities.NotificationEntity;

public interface InsertNotificationService  {
    Result<NotificationEntity> execute(NotificationEntity noti);
}
