package com.read.api.application.usecase.impl.cdc.urlAccessRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.cdc.urlAccessRule.UrlAccessRuleCdcServiceUse;
import com.read.api.application.usecase.interfaces.urlAccessRule.DeleteUrlAccessRuleByIdUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.InsertUrlAccessRuleUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.SaveUrlAccessRuleUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlAccessRuleCdcEvent;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.metrics.observed.ObservedMetric;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlAccessRuleCdcServiceUseImpl implements UrlAccessRuleCdcServiceUse {

    InsertUrlAccessRuleUseCase insert;
    SaveUrlAccessRuleUseCase save;
    DeleteUrlAccessRuleByIdUseCase delete;
    RedisCrudService redis;
    UrlAccessRuleCdcMapper mapper;

    @Override
    @Retry(name = "cdc-action")
    @ObservedMetric("url.access.rule.service.cdc")
    public void process(
            TiCdcEvent<UrlAccessRuleCdcEvent> event
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
            log.error("Error processing UrlAccessRule CDC event: {}", eventId, exception);
        }
    }

    private boolean processInsert(
            TiCdcEvent<UrlAccessRuleCdcEvent> event
    ) {

        var rule = mapper.toModel(event.firstData());

        var result = insert.execute(rule);

        return result.isSuccess();
    }

    private boolean processUpdate(
            TiCdcEvent<UrlAccessRuleCdcEvent> event
    ) {

        var rule = mapper.toModel(event.firstData());

        var result = save.execute(rule);

        return result.isSuccess();
    }

    private boolean processDelete(
            TiCdcEvent<UrlAccessRuleCdcEvent> event
    ) {

        if (event.old() == null || event.old().isEmpty()) return false;

        Long ruleId = event.old().getFirst().id();

        var result = delete.execute(ruleId);

        return result.isSuccess();
    }
}
