package com.read.api.infrastructure.kafka.consumer.urlRedirectRule;

import com.read.api.application.usecase.interfaces.cdc.urlRedirectRule.UrlRedirectRuleCdcServiceUseCase;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlRedirectRuleCdcEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlRedirectRuleCdcConsumer {

    UrlRedirectRuleCdcServiceUseCase service;

    @KafkaListener(
            topics = "url_redirect_rules",
            groupId = "url-shortener"
    )
    public void consume(
            TiCdcEvent<UrlRedirectRuleCdcEvent> event
    ) {
        service.process(event);
    }
}
