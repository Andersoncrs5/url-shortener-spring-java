package com.read.api.infrastructure.kafka.consumer.urlRedirectRule;

import com.read.api.application.usecase.interfaces.cdc.urlRedirectRule.UrlRedirectRuleCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlRedirectRuleCdcEvent;
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
public class UrlRedirectRuleCdcConsumer extends AbstractCdcConsumer<UrlRedirectRuleCdcEvent> {

    UrlRedirectRuleCdcServiceUseCase service;

    public UrlRedirectRuleCdcConsumer(
            UrlRedirectRuleCdcServiceUseCase service,
            DeadLetterPublisher deadLetterPublisher
    ) {
        super(deadLetterPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "url_redirect_rules",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            TiCdcEvent<UrlRedirectRuleCdcEvent> event
    ) {
        process(
                event,
                () -> service.process(event),
                TopicEnum.URL_REDIRECT_RULE_DLQ
        );
    }
}