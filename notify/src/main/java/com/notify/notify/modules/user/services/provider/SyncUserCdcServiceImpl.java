package com.notify.notify.modules.user.services.provider;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.globals.services.redis.RedisCrudService;
import com.notify.notify.modules.user.dto.UserCdcEvent;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.mapper.UserMapper;
import com.notify.notify.modules.user.services.base.DeleteUserByIdService;
import com.notify.notify.modules.user.services.base.InsertUserService;
import com.notify.notify.modules.user.services.base.SyncUserCdcService;
import com.notify.notify.modules.user.services.base.SyncUserService;
import com.notify.notify.utils.annotations.UseService;
import com.notify.notify.utils.annotations.metrics.ObservedMetric;
import com.notify.notify.utils.annotations.tx.ResultTransaction;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SyncUserCdcServiceImpl implements SyncUserCdcService {

    SyncUserService syncUser;
    InsertUserService insertUser;
    DeleteUserByIdService deleteUserById;
    RedisCrudService redis;
    UserMapper mapper;

    @Override
    @ResultTransaction
    @Retry(name = "cdc-action")
    @ObservedMetric("user.service.cdc")
    public Result<UserEntity> execute(
            TiCdcEvent<UserCdcEvent> event
    ) {

        String eventId = event.table() + ":" + event.ts() + ":" + event.es();

        if (redis.exists(eventId)) {
            log.debug("Ignoring duplicated CDC event: {}", eventId);

            return Result.success();
        }


        try {

            Result<UserEntity> result;

            if (event.isInsert()) {
                result = processInsert(event);
            } else if (event.isUpdate()) {
                result = processUpdate(event);
            } else if (event.isDelete()) {
                result = processDelete(event);
            } else {
                log.warn("Unsupported CDC event: {}", eventId);

                throw new BusinessException("Unsupported CDC event");
            }

            if (result.isSuccess()) {
                redis.save(eventId, "processed", Duration.ofDays(2));
            }

            return result;
        } catch (Exception exception) {
            log.error("Error processing CDC event: {}", eventId, exception);

            throw new BusinessException(
                    exception.getMessage()
            );
        }
    }

    private Result<UserEntity> processInsert(
            TiCdcEvent<UserCdcEvent> event
    ) {
        UserEntity user = mapper.toEntity(event.firstData());

        Result<UserEntity> result = insertUser.execute(user);

        if (result.isFailure()) {
            String msg = result.getMessage().orElse(null);

            log.error("Error inserting user: {}! Message: {}", user.getEmail(), msg);

            return result;
        }

        log.info("User inserted successfully: {}", user.getEmail());

        return result;
    }

    private Result<UserEntity> processUpdate(
            TiCdcEvent<UserCdcEvent> event
    ) {
        UserEntity user = mapper.toEntity(event.firstData());

        Result<UserEntity> result = syncUser.execute(user);

        if (result.isFailure()) {
            log.error("Error updating user: {}", user.getEmail());
            return result;
        }

        log.info("User updated successfully: {}", user.getEmail());

        return result;
    }

    private Result<UserEntity> processDelete(
            TiCdcEvent<UserCdcEvent> event
    ) {
        if (event.old() == null || event.old().isEmpty()) {

            log.error(
                    "Delete CDC event without old data"
            );

            throw new BusinessException(
                    "Delete event without old data"
            );
        }

        Long userId = event.old().getFirst().id();

        Result<Void> result = deleteUserById.execute(userId);

        if (result.isFailure()) {
            log.error(
                    "Error deleting user: {}",
                    userId
            );

            return Result.failure(
                    result.getErrors(),
                    result.getStatusCode()
            );
        }

        log.info(
                "User deleted successfully: {}",
                userId
        );


        return Result.success();
    }
}