package com.read.api.infrastructure.kafka.consumer.url;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.interfaces.cdc.url.UrlCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
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
public class UrlCdcConsumer extends AbstractCdcConsumer<UrlCdcEvent> {

    UrlCdcServiceUseCase service;
    ObjectMapper objectMapper;

    public UrlCdcConsumer(
            UrlCdcServiceUseCase service,
            DeadLetterPublisher deadLetterPublisher, ObjectMapper objectMapper
    ) {
        super(deadLetterPublisher);
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "urls",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            String payload
    ) {
        try {
            TiCdcEvent<UrlCdcEvent> event =
                    objectMapper.readValue(
                            payload,
                            new TypeReference<TiCdcEvent<UrlCdcEvent>>() {}
                    );

            process(
                    event,
                    () -> service.process(event),
                    TopicEnum.URLS_DLQ
            );
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}