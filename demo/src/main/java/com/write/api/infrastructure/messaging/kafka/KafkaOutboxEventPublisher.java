package com.write.api.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.exception.CircuitBreakerException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaOutboxEventPublisher implements OutboxEventPublisher {

    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "kafka", fallbackMethod = "fallbackPublish")
    @Retry(name = "kafka")
    @Bulkhead(name = "kafka")
    @TrackExecutionTime("kafka.publish")
    public SendResult<String, String> publish(OutboxEventModel event) {

        OutboxEventMessage message = new OutboxEventMessage(
                String.valueOf(event.getId()),
                event.getAggregateType().name(),
                event.getAggregateId(),
                event.getEventType().name(),
                event.getTopic().name(),
                event.getPayload(),
                event.getVersion()
        );

        try {
            String json = objectMapper.writeValueAsString(message);

            return kafkaTemplate.send(
                    message.topic(),
                    message.aggregateId().toString(),
                    json
            ).toCompletableFuture().join();

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }

    @SuppressWarnings("unused")
    private SendResult<String, String> fallbackPublish(
            OutboxEventModel event,
            Throwable ex
    ) {
        throw new CircuitBreakerException(
                "Kafka publish failed after retries: " + event.getId(),
                ex
        );
    }

}