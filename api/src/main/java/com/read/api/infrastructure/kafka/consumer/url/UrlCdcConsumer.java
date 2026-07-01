package com.read.api.infrastructure.kafka.consumer.url;

import com.read.api.application.usecase.interfaces.cdc.url.UrlCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.infrastructure.kafka.base.AbstractCdcConsumer;
import com.read.api.infrastructure.kafka.dlq.DeadLetterPublisher;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlCdcConsumer extends AbstractCdcConsumer<UrlCdcEvent> {

    UrlCdcServiceUseCase service;

    public UrlCdcConsumer(
            UrlCdcServiceUseCase service,
            DeadLetterPublisher deadLetterPublisher
    ) {
        super(deadLetterPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "urls",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            TiCdcEvent<UrlCdcEvent> event
    ) {
        process(
                event,
                () -> service.process(event),
                TopicEnum.URLS_DLQ
        );
    }
}