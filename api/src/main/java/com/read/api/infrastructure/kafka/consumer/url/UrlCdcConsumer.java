package com.read.api.infrastructure.kafka.consumer.url;

import com.read.api.application.usecase.interfaces.cdc.url.UrlCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlCdcConsumer {

    UrlCdcServiceUseCase service;

    @KafkaListener(
            topics = "urls",
            groupId = "url-shortener"
    )
    public void consume(
            TiCdcEvent<UrlCdcEvent> event
    ) {
        service.process(event);
    }
}
