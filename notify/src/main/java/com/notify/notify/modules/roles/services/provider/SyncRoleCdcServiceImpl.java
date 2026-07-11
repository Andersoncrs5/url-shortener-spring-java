package com.notify.notify.modules.roles.services.provider;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.globals.services.redis.RedisCrudService;
import com.notify.notify.modules.roles.dto.RoleCdcEvent;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.mapper.RoleMapper;
import com.notify.notify.modules.roles.services.base.DeleteRoleByIdService;
import com.notify.notify.modules.roles.services.base.SyncRoleCdcService;
import com.notify.notify.modules.roles.services.base.SyncRoleService;
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
public class SyncRoleCdcServiceImpl implements SyncRoleCdcService {

    RedisCrudService redis;
    SyncRoleService sync;
    DeleteRoleByIdService delete;
    RoleMapper mapper;

    @Override
    @ResultTransaction
    @Retry(name = "cdc-action")
    @ObservedMetric("role.service.cdc")
    public Result<RoleEntity> execute(TiCdcEvent<RoleCdcEvent> event) {
        String eventId = event.table() + ":" + event.ts() + ":" + event.es();

        if (redis.exists(eventId)) {
            log.debug("Ignoring duplicated CDC event: {}", eventId);
            return Result.success();
        }

        try {
            Result<RoleEntity> result;

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
            throw new BusinessException(exception.getMessage());
        }
    }

    private Result<RoleEntity> processInsert(TiCdcEvent<RoleCdcEvent> event) {
        RoleEntity role = mapper.toEntity(event.firstData());
        Result<RoleEntity> result = sync.execute(role);

        if (result.isFailure()) {
            log.error("Error inserting role: {}", role.getName());
            return result;
        }

        log.info("Role inserted successfully: {}", role.getName());
        return result;
    }

    private Result<RoleEntity> processUpdate(TiCdcEvent<RoleCdcEvent> event) {
        RoleEntity role = mapper.toEntity(event.firstData());
        Result<RoleEntity> result = sync.execute(role);

        if (result.isFailure()) {
            log.error("Error updating role: {}", role.getName());
            return result;
        }

        log.info("Role updated successfully: {}", role.getName());
        return result;
    }

    private Result<RoleEntity> processDelete(TiCdcEvent<RoleCdcEvent> event) {
        if (event.old() == null || event.old().isEmpty()) {
            log.error("Delete CDC event without old data");
            throw new BusinessException("Delete event without old data");
        }

        Long roleId = event.old().getFirst().id();
        Result<Void> result = delete.execute(roleId);

        if (result.isFailure()) {
            log.error("Error deleting role: {}", roleId);
            return Result.failure(result.getErrors(), result.getStatusCode());
        }

        log.info("Role deleted successfully: {}", roleId);
        return Result.success();
    }
}