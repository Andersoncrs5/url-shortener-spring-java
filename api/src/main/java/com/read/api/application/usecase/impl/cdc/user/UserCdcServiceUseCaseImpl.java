package com.read.api.application.usecase.impl.cdc.user;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.cdc.user.UserCdcServiceUseCase;
import com.read.api.application.usecase.interfaces.user.DeleteByIdUserUseCase;
import com.read.api.application.usecase.interfaces.user.InsertUserUseCase;
import com.read.api.application.usecase.interfaces.user.SaveUserUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCdcServiceUseCaseImpl implements UserCdcServiceUseCase {

    InsertUserUseCase insert;
    SaveUserUseCase save;
    DeleteByIdUserUseCase delete;
    RedisCrudService redis;
    UserCdcMapper mapper;

    @Override
    @Retry(name = "cdc-action")
    @ObservedMetric("user.service.cdc")
    public void process(
            TiCdcEvent<UserCdcEvent> event
    ) {
        String eventId = event.table() + ":" + event.ts() + ":" + event.es();

        if (redis.exists(eventId)) {
            log.debug(
                    "Ignoring duplicated CDC event: {}",
                    eventId
            );

            return;
        }

        boolean success = false;

        try {
            if (event.isInsert()) {
                success = processInsert(event);
            } else if (event.isUpdate()) {
                success = processUpdate(event);
            } else if (event.isDelete()) {
                success = processDelete(event);
            }

            if (success) {
                redis.save(eventId, "processed");
            }

        } catch (Exception exception) {
            log.error("Error processing CDC event: {}", eventId, exception);
        }
    }

    private boolean processInsert(
            TiCdcEvent<UserCdcEvent> event
    ) {
        UserModel user =
                mapper.toModel(
                        event.firstData()
                );

        Result<UserModel> result =
                insert.execute(user);

        if (result.isFailure()) {

            log.error(
                    "Error inserting user: {}",
                    user.getEmail()
            );

            return false;
        }

        log.info(
                "User inserted: {}",
                user.getEmail()
        );

        return true;
    }

    private boolean processUpdate(
            TiCdcEvent<UserCdcEvent> event
    ) {
        UserModel user =
                mapper.toModel(
                        event.firstData()
                );

        Result<UserModel> result =
                save.execute(user);

        if (result.isFailure()) {

            log.error(
                    "Error updating user: {}",
                    user.getEmail()
            );

            return false;
        }

        log.info(
                "User updated: {}",
                user.getEmail()
        );

        return true;
    }

    private boolean processDelete(
            TiCdcEvent<UserCdcEvent> event
    ) {

        if (event.old() == null
                || event.old().isEmpty()) {

            log.error(
                    "Delete event without old data"
            );

            return false;
        }

        Long userId =
                event.old()
                        .getFirst()
                        .id();

        Result<Void> result =
                delete.execute(userId);

        if (result.isFailure()) {

            log.error(
                    "Error deleting user: {}",
                    userId
            );

            return false;
        }

        log.info(
                "User deleted: {}",
                userId
        );

        return true;
    }
}