package com.notify.notify.modules.userRole.services.provider;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.globals.services.redis.RedisCrudService;
import com.notify.notify.modules.userRole.dto.UserRoleCdcEvent;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import com.notify.notify.modules.userRole.mapper.UserRoleMapper;
import com.notify.notify.modules.userRole.services.base.DeleteUserRoleService;
import com.notify.notify.modules.userRole.services.base.InsertUserRoleService;
import com.notify.notify.modules.userRole.services.base.SyncUserRoleCdcService;
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
public class SyncUserRoleCdcServiceImpl implements SyncUserRoleCdcService {

    InsertUserRoleService insertUserRole;
    DeleteUserRoleService deleteUserRole;
    RedisCrudService redis;
    UserRoleMapper mapper;

    @Override
    @ResultTransaction
    @Retry(name = "cdc-action")
    @ObservedMetric("user.role.service.cdc")
    public Result<UserRoleEntity> execute(TiCdcEvent<UserRoleCdcEvent> event) {
        String eventId = event.table() + ":" + event.ts() + ":" + event.es();

        if (redis.exists(eventId)) {
            log.debug("Ignoring duplicated UserRole CDC event: {}", eventId);
            return Result.success();
        }

        try {
            Result<UserRoleEntity> result;

            if (event.isInsert()) {
                result = processInsert(event);
            } else if (event.isUpdate()) {
                result = processInsert(event);
            } else if (event.isDelete()) {
                result = processDelete(event);
            } else {
                log.warn("Unsupported UserRole CDC event: {}", eventId);
                throw new BusinessException("Unsupported CDC event");
            }

            if (result.isSuccess()) {
                redis.save(eventId, "processed", Duration.ofDays(2));
            }

            return result;
        } catch (Exception exception) {
            log.error("Error processing UserRole CDC event: {}", eventId, exception);
            throw new BusinessException(exception.getMessage());
        }
    }

    private Result<UserRoleEntity> processInsert(TiCdcEvent<UserRoleCdcEvent> event) {
        UserRoleEntity userRole = mapper.toEntity(event.firstData());

        Result<UserRoleEntity> result = insertUserRole.execute(userRole);

        if (result.isFailure()) {
            String msg = result.getMessage().orElse(null);
            log.error("Error inserting UserRole mapping (User: {}, Role: {})! Message: {}",
                    userRole.getUserId(), userRole.getRoleId(), msg);
            return result;
        }

        log.info("UserRole mapping inserted successfully. User: {}, Role: {}",
                userRole.getUserId(), userRole.getRoleId());

        return result;
    }

    private Result<UserRoleEntity> processDelete(TiCdcEvent<UserRoleCdcEvent> event) {
        if (event.old() == null || event.old().isEmpty()) {
            log.error("Delete UserRole CDC event without old data");
            throw new BusinessException("Delete event without old data");
        }

        Long userRoleId = event.old().getFirst().id();

        Result<Void> result = deleteUserRole.execute(userRoleId);

        if (result.isFailure()) {
            log.error("Error deleting UserRole mapping ID: {}", userRoleId);
            return Result.failure(result.getErrors(), result.getStatusCode());
        }

        log.info("UserRole mapping deleted successfully ID: {}", userRoleId);

        return Result.success();
    }
}