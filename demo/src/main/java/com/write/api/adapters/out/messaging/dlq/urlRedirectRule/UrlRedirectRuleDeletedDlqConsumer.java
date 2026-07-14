package com.write.api.adapters.out.messaging.dlq.urlRedirectRule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.dto.outbox.events.urlRedirectRule.UrlRedirectRuleCreatedEvent;
import com.write.api.application.dto.outbox.events.urlRedirectRule.UrlRedirectRuleDeletedEvent;
import com.write.api.application.shared.Result;
import com.write.api.ports.in.outbox.HandlerDlqUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlRedirectRuleDeletedDlqConsumer {

    HandlerDlqUseCase handlerDlq;

    @KafkaListener(
            topics = "url.redirect.rule.created.dlq",
            groupId = "url-shortener-dlq"
    )
    public void consume(String payload) {

        Result<Void> result = handlerDlq.execute(
                payload,
                new TypeReference<OutboxEventMessage<UrlRedirectRuleDeletedEvent>>() {}
        );

        if (result.isFailure()) {
            log.error(
                    "Failed to reprocess DLQ event. Status: {}, Message: {}",
                    result.getStatusCode(),
                    result.getMessage()
            );
            return;
        }

        log.info("DLQ event successfully reprocessed");
    }
}