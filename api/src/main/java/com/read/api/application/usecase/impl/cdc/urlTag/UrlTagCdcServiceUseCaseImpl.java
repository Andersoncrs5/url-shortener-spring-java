package com.read.api.application.usecase.impl.cdc.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.cdc.urlTag.UrlTagCdcServiceUseCase;
import com.read.api.application.usecase.interfaces.urlTag.DeleteUrlTagByIdUseCase;
import com.read.api.application.usecase.interfaces.urlTag.InsertUrlTagUseCase;
import com.read.api.application.usecase.interfaces.urlTag.SaveUrlTagUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlTagCdcEvent;
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
public class UrlTagCdcServiceUseCaseImpl implements UrlTagCdcServiceUseCase {

    InsertUrlTagUseCase insert;
    SaveUrlTagUseCase save;
    DeleteUrlTagByIdUseCase delete;
    RedisCrudService redis;
    UrlTagCdcMapper mapper;

    @Override
    @ObservedMetric("url.tag.service.cdc")
    public void process(
            TiCdcEvent<UrlTagCdcEvent> event
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
                    "Error processing UrlTag CDC event: {}",
                    eventId,
                    exception
            );

        }
    }

    private boolean processInsert(
            TiCdcEvent<UrlTagCdcEvent> event
    ) {

        var tag =
                mapper.toModel(
                        event.firstData()
                );

        var result =
                insert.execute(tag);

        return result.isSuccess();
    }

    private boolean processUpdate(
            TiCdcEvent<UrlTagCdcEvent> event
    ) {

        var tag =
                mapper.toModel(
                        event.firstData()
                );

        var result =
                save.execute(tag);

        return result.isSuccess();
    }

    private boolean processDelete(
            TiCdcEvent<UrlTagCdcEvent> event
    ) {

        if (event.old() == null
                || event.old().isEmpty()) {

            return false;
        }

        Long tagId =
                event.old()
                        .getFirst()
                        .id();

        var result =
                delete.execute(tagId);

        return result.isSuccess();
    }
}
