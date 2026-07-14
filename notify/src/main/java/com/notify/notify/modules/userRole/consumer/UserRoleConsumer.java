package com.notify.notify.modules.userRole.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.configs.kafka.dlq.DeadLetterPublisher;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.enums.TopicEnum;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.modules.userRole.dto.UserRoleCdcEvent;
import com.notify.notify.modules.userRole.services.base.SyncUserRoleCdcService;
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
public class UserRoleConsumer extends AbstractCdcConsumer<UserRoleCdcEvent> {

    SyncUserRoleCdcService service;
    ObjectMapper mapper;

    protected UserRoleConsumer(DeadLetterPublisher deadLetterPublisher, SyncUserRoleCdcService service, ObjectMapper mapper) {
        super(deadLetterPublisher);
        this.service = service;
        this.mapper = mapper;
    }

    @KafkaListener(
            topics = "user_roles",
            groupId = "notify-api"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(String payload) {
        try {
            TiCdcEvent<UserRoleCdcEvent> event =
                    mapper.readValue(
                            payload,
                            new TypeReference<>() {}
                    );

            process(
                    event,
                    () -> service.execute(event),
                    TopicEnum.USER_ROLES_DLQ
            );
        } catch (Exception e) {
            log.error("Error processing user_roles CDC payload: {}", e.getMessage(), e);
            throw new BusinessException(e.getMessage());
        }
    }
}