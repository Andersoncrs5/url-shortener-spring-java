package com.notify.notify.modules.user.consumer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.configs.kafka.dlq.DeadLetterPublisher;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.enums.TopicEnum;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.modules.user.dto.UserCdcEvent;
import com.notify.notify.modules.user.services.base.SyncUserCdcService;
import com.notify.notify.utils.base.cdc.AbstractCdcConsumer;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserConsumer extends AbstractCdcConsumer<UserCdcEvent> {

    SyncUserCdcService service;
    ObjectMapper mapper;

    protected UserConsumer(DeadLetterPublisher deadLetterPublisher, SyncUserCdcService service, ObjectMapper mapper) {
        super(deadLetterPublisher);
        this.service = service;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "users",
            groupId = "notify-api"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(String payload) {

        try {
            TiCdcEvent<UserCdcEvent> event =
                    mapper.readValue(
                            payload,
                            new TypeReference<>() {}
                    );

            process(
                    event,
                    () -> service.execute(event),
                    TopicEnum.USERS_DLQ
            );
        } catch (Exception e) {

            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }
}
