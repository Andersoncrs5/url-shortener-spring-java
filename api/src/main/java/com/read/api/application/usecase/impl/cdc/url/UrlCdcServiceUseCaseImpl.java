package com.read.api.application.usecase.impl.cdc.url;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.cdc.url.UrlCdcServiceUseCase;
import com.read.api.application.usecase.interfaces.url.DeleteUrlByIdUseCase;
import com.read.api.application.usecase.interfaces.url.InsertUrlUseCase;
import com.read.api.application.usecase.interfaces.url.SaveUrlUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.service.RedisCrudService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlCdcServiceUseCaseImpl implements UrlCdcServiceUseCase {

    InsertUrlUseCase insert;
    SaveUrlUseCase save;
    DeleteUrlByIdUseCase delete;
    RedisCrudService redis;
    UrlCdcMapper mapper;

    @Override
    public void process(
            TiCdcEvent<UrlCdcEvent> event
    ) {

        String eventId = event.table() + ":" + event.ts() + ":" + event.es();

        if (redis.exists(eventId)) {
            log.debug("Ignoring duplicated CDC event: {}", eventId);

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
            log.error("Error processing URL CDC event: {}", eventId, exception);
        }
    }

    private boolean processInsert(
            TiCdcEvent<UrlCdcEvent> event
    ) {

        var url = mapper.toModel(event.firstData());

        var result = insert.execute(url);

        if (result.isFailure()) {

            log.error("Error inserting url: {}", url.getId());

            return false;
        }

        log.info("Url inserted: {}", url.getId());

        return true;
    }

    private boolean processUpdate(
            TiCdcEvent<UrlCdcEvent> event
    ) {

        var url = mapper.toModel(event.firstData());

        var result = save.execute(url);

        if (result.isFailure()) {

            log.error(
                    "Error updating url: {}",
                    url.getId()
            );

            return false;
        }

        log.info(
                "Url updated: {}",
                url.getId()
        );

        return true;
    }

    private boolean processDelete(
            TiCdcEvent<UrlCdcEvent> event
    ) {
        if (event.old() == null || event.old().isEmpty()) {
            log.error("Delete event without old data");

            return false;
        }

        Long urlId = event.old().getFirst().id();

        var result =
                delete.execute(urlId);

        if (result.isFailure()) {

            log.error(
                    "Error deleting url: {}",
                    urlId
            );

            return false;
        }

        log.info(
                "Url deleted: {}",
                urlId
        );

        return true;
    }
}