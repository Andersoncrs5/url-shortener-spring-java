package com.read.api.infrastructure.kafka.consumer.user;

import com.read.api.application.usecase.interfaces.cdc.user.UserCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;
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
public class UserCdcConsumer extends AbstractCdcConsumer<UserCdcEvent> {

    UserCdcServiceUseCase service;

    public UserCdcConsumer(
            UserCdcServiceUseCase service,
            DeadLetterPublisher deadLetterPublisher
    ) {
        super(deadLetterPublisher);
        this.service = service;
    }

    @KafkaListener(
            topics = "users",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            TiCdcEvent<UserCdcEvent> event
    ) {
        process(
                event,
                () -> service.process(event),
                TopicEnum.USERS_DLQ
        );
    }
}