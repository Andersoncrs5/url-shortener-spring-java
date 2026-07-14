package com.notify.notify.modules.notifications.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.notifications.entities.NotificationEntity;
import com.notify.notify.modules.notifications.repository.NotificationRepository;
import com.notify.notify.modules.notifications.services.base.InsertNotificationService;
import com.notify.notify.utils.annotations.UseService;
import com.notify.notify.utils.annotations.metrics.ObservedMetric;
import com.notify.notify.utils.annotations.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertNotificationServiceImpl implements InsertNotificationService {

    NotificationRepository repository;

    @Override
    @ResultTransaction
    @ObservedMetric("insert.notification.service")
    public Result<NotificationEntity> execute(NotificationEntity noti) {
        try {
            repository.insert(noti);

            return Result.success(noti, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
