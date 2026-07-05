package com.read.api.infrastructure.kafka.consumer.urlAccessRule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.interfaces.cdc.urlAccessRule.UrlAccessRuleCdcServiceUse;
import com.read.api.application.usecase.interfaces.cdc.urlRedirectRule.UrlRedirectRuleCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlAccessRuleCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.cdc.classes.UrlRedirectRuleCdcEvent;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.infrastructure.kafka.base.AbstractCdcConsumer;
import com.read.api.infrastructure.kafka.dlq.DeadLetterPublisher;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlAccessRuleCdcConsumer extends AbstractCdcConsumer<UrlAccessRuleCdcEvent> {

    UrlAccessRuleCdcServiceUse service;
    ObjectMapper mapper;

    public UrlAccessRuleCdcConsumer(
            UrlAccessRuleCdcServiceUse service,
            DeadLetterPublisher deadLetterPublisher, ObjectMapper mapper
    ) {
        super(deadLetterPublisher);
        this.service = service;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "url_access_rule",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            String payload
    ) {

        try {
            TiCdcEvent<UrlAccessRuleCdcEvent> event =
                    mapper.readValue(
                            payload,
                            new TypeReference<TiCdcEvent<UrlAccessRuleCdcEvent>>() {}
                    );

            process(
                    event,
                    () -> service.process(event),
                    TopicEnum.URL_ACCESS_RULE_DLQ
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}