package com.read.api.infrastructure.kafka.consumer.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.interfaces.deadLetterEvent.InsertDeadLetterEventUseCase;
import com.read.api.domain.cdc.classes.UserCdcEvent;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.infrastructure.kafka.base.AbstractDlqConsumer;
import com.read.api.infrastructure.kafka.dlq.DeadLetterEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCdcConsumerDlq
        extends AbstractDlqConsumer<UserCdcEvent> {

    public UserCdcConsumerDlq(
            RedisCrudService cache,
            InsertDeadLetterEventUseCase insert,
            ObjectMapper mapper
    ) {
        super(cache, insert, mapper);
    }

    @KafkaListener(
            topics = "users.dlq",
            groupId = "url-shortener"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(
            ConsumerRecord<String, DeadLetterEvent<UserCdcEvent>> record

    ) {
        saveDeadLetter(
                record,
                TopicEnum.USERS,
                TopicEnum.USERS_DLQ,
                "users"
        );
    }
}
