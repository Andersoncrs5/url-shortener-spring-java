package com.notify.notify.modules.roles.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.configs.kafka.dlq.DeadLetterPublisher;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.enums.TopicEnum;
import com.notify.notify.modules.roles.dto.RoleCdcEvent;
import com.notify.notify.modules.roles.services.base.SyncRoleCdcService;
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
public class RoleConsumer extends AbstractCdcConsumer<RoleCdcEvent> {
    SyncRoleCdcService service;
    ObjectMapper mapper;

    protected RoleConsumer(DeadLetterPublisher deadLetterPublisher, SyncRoleCdcService service, ObjectMapper mapper) {
        super(deadLetterPublisher);
        this.service = service;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "roles",
            groupId = "notify-api"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(String payload) {

        try {
            TiCdcEvent<RoleCdcEvent> event =
                    mapper.readValue(
                            payload,
                            new TypeReference<>() {}
                    );

            process(
                    event,
                    () -> service.execute(event),
                    TopicEnum.ROLES_DLQ
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }

}
