package com.read.api.infrastructure.kafka.consumer.urlTag;

import com.read.api.application.usecase.interfaces.cdc.urlTag.UrlTagCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlTagCdcEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlTagCdcConsumer {

    UrlTagCdcServiceUseCase service;

    @KafkaListener(
            topics = "url_redirect_rules",
            groupId = "url-shortener"
    )
    public void consume(
            TiCdcEvent<UrlTagCdcEvent> event
    ) {
        service.process(event);
    }

}
