package com.read.api.infrastructure.kafka.consumer.role;

import com.read.api.application.usecase.interfaces.cdc.role.RoleCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;
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
public class RoleCdcConsumer extends AbstractCdcConsumer<RoleCdcEvent> {

    RoleCdcServiceUseCase service;

    public RoleCdcConsumer(
            RoleCdcServiceUseCase service,
            DeadLetterPublisher deadLetterPublisher
    ) {
        super(deadLetterPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "roles",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            TiCdcEvent<RoleCdcEvent> event
    ) {
        process(
                event,
                () -> service.process(event),
                TopicEnum.ROLES_DLQ
        );
    }
}
