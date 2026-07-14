package com.notify.notify.modules.notifications.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.notify.notify.globals.classes.outbox.OutboxCdcEvent;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.modules.notifications.services.base.WelcomeMessage;
import com.notify.notify.utils.base.consumer.BaseConsumer;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WelcomeMessageConsumer extends BaseConsumer {

    WelcomeMessage welcome;

    @KafkaListener(
            topics = "notify",
            groupId = "notify-api"
    )
    @Retry(name = "kafka")
    @CircuitBreaker(name = "kafka")
    @Bulkhead(name = "kafka")
    public void consume(String payload) {
        OutboxCdcEvent outboxEvent = null;
        try {
            outboxEvent = mapper.readValue(payload, new TypeReference<>() {});

            var check = check(outboxEvent);
            if (check.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                log.info("Event already processed or in execution: {}", outboxEvent.eventId());
                return;
            }

            var result = welcome.execute(outboxEvent);

            if (result.isFailure()) {
                String errorMsg = result.getMessage().orElse("Unknown error");
                log.error("Failed to process welcome message for eventId={}. Reason: {}", outboxEvent.eventId(), errorMsg);

                invalidateLock(outboxEvent);
                throw new BusinessException("Processing failed: " + errorMsg);
            }

        } catch (Exception e) {
            log.error("Critical error processing outbox notification message", e);

            if (outboxEvent != null) {
                invalidateLock(outboxEvent);
            }
            throw new BusinessException(e.getMessage());
        }
    }
}