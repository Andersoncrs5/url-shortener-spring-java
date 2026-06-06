package com.write.api.adapters.out.messaging.dlq.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.dto.outbox.events.user.UserDeletedEvent;
import com.write.api.application.shared.Result;
import com.write.api.ports.in.outbox.HandlerDlqUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletedDlqConsumer {

    private final HandlerDlqUseCase handlerDlq;

    @KafkaListener(
            topics = "user.deleted.dlq",
            groupId = "url-shortener-dlq"
    )
    public void consume(String payload) {

        Result<Void> result = handlerDlq.execute(
                payload,
                new TypeReference<OutboxEventMessage<UserDeletedEvent>>() {}
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