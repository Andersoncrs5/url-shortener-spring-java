package com.read.api.application.usecase.impl.cdc.role;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.cdc.role.RoleCdcServiceUseCase;
import com.read.api.application.usecase.interfaces.role.DeleteRoleByIdUseCase;
import com.read.api.application.usecase.interfaces.role.InsertRoleUseCase;
import com.read.api.application.usecase.interfaces.role.SaveRoleUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.metrics.observed.ObservedMetric;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleServiceUseCaseImpl implements RoleCdcServiceUseCase {

    InsertRoleUseCase insert;
    SaveRoleUseCase save;
    DeleteRoleByIdUseCase delete;
    RedisCrudService redis;
    RoleCdcMapper mapper;

    @Override
    @ObservedMetric("role.service.cdc")
    public void process(
            TiCdcEvent<RoleCdcEvent> event
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
                redis.save(
                        eventId,
                        "processed"
                );
            }

        } catch (Exception exception) {

            log.error(
                    "Error processing ApiKey CDC event: {}",
                    eventId,
                    exception
            );

        }
    }

    private boolean processInsert(
            TiCdcEvent<RoleCdcEvent> event
    ) {

        var apiKey =
                mapper.toModel(
                        event.firstData()
                );

        var result =
                insert.execute(apiKey);

        if (result.isFailure()) {
            return false;
        }

        return true;
    }

    private boolean processUpdate(
            TiCdcEvent<RoleCdcEvent> event
    ) {

        var apiKey =
                mapper.toModel(
                        event.firstData()
                );

        var result =
                save.execute(apiKey);

        if (result.isFailure()) {
            return false;
        }

        return true;
    }

    private boolean processDelete(
            TiCdcEvent<RoleCdcEvent> event
    ) {

        if (event.old() == null
                || event.old().isEmpty()) {

            return false;
        }

        Long apiKeyId =
                event.old()
                        .getFirst()
                        .id();

        var result =
                delete.execute(apiKeyId);

        return result.isSuccess();
    }
}