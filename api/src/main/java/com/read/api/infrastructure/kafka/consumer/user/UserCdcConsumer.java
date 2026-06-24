package com.read.api.infrastructure.kafka.consumer.user;

import com.read.api.application.usecase.interfaces.cdc.user.UserCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserCdcConsumer {

    UserCdcServiceUseCase service;

    @KafkaListener(
            topics = "users",
            groupId = "url-shortener"
    )
    public void consume(
            TiCdcEvent<UserCdcEvent> event
    ) {
        service.process(event);
    }
}
