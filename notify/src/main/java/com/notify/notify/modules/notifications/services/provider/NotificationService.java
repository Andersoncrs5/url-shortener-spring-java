package com.notify.notify.modules.notifications.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.notifications.entities.NotificationEntity;
import com.notify.notify.modules.notifications.repository.NotificationRepository;
import com.notify.notify.modules.notifications.services.base.INotificationService;
import com.notify.notify.utils.annotations.UseService;
import com.notify.notify.utils.annotations.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService implements INotificationService {
    NotificationRepository repository;

//    @ResultTransaction
//    public Result<NotificationEntity> create() {
//
//    }

    @ResultTransaction(readOnly = true)
    public Result<NotificationEntity> findById(Long id) {
        Optional<NotificationEntity> optional = repository.findById(id);

        return optional.map(notificationEntity -> Result.success(notificationEntity, HttpStatus.OK))
                .orElseGet(() -> Result.failure("Notification not found", HttpStatus.NOT_FOUND));
    }

    @ResultTransaction
    public Result<Void> delete(Long id) {
        int count = repository.deleteAndCount(id);

        if (count <= 0) {
            return Result.failure("Notification not found", HttpStatus.NOT_FOUND);
        }

        return Result.success(HttpStatus.OK);
    }

}
