package com.write.api.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.core.domain.exception.CircuitBreakerException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaOutboxEventPublisher implements OutboxEventPublisher {

    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

    @Override
    public SendResult<String, String> publish(OutboxEventModel event) {
        String topic = event.getTopic().value().toLowerCase();

        OutboxEventMessage message = new OutboxEventMessage(
                String.valueOf(event.getId()),
                event.getAggregateType().name(),
                event.getAggregateId(),
                event.getEventType().name(),
                topic,
                event.getPayload(),
                event.getVersion()
        );

        try {
            String json = objectMapper.writeValueAsString(message);
            log.info("Send event to topic: {}, id: {} ", topic, message.eventId());

            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(
                    topic,
                    message.aggregateId().toString(),
                    json
            );

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Outbox event {} delivered successfully!", event.getId());
                } else {
                    log.error("Kafka async delivery failed for event {}", event.getId(), ex);
                }
            });

            return future.join();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        } catch (CompletionException e) {
            throw new RuntimeException("Kafka delivery failed", e.getCause());
        }
    }

    @SuppressWarnings("unused")
    private SendResult<String, String> fallbackPublish(
            OutboxEventModel event,
            Throwable ex
    ) {
        log.error(
                "Kafka publish failed. eventId={}, topic={}, aggregateId={} cause={}",
                event.getId(),
                event.getTopic(),
                event.getAggregateId(),
                ex.getMessage(),
                ex
        );

        throw new CircuitBreakerException(
                "Kafka publish failed after retries: " + event.getId(),
                ex
        );
    }
}