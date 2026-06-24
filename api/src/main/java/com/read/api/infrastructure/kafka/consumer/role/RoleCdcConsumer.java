package com.read.api.infrastructure.kafka.consumer.role;

import com.read.api.application.usecase.interfaces.cdc.role.RoleCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleCdcConsumer {

    RoleCdcServiceUseCase service;

    @KafkaListener(
            topics = "roles",
            groupId = "url-shortener"
    )
    public void consume(
            TiCdcEvent<RoleCdcEvent> event
    ) {
        service.process(event);
    }
}
