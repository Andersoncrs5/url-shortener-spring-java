package com.read.api.infrastructure.kafka.dlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeadLetterPublisherImpl implements DeadLetterPublisher {

    KafkaTemplate<String, Object> kafkaTemplate;
    SnowflakeIdGenerator generator;
    ObjectMapper mapper;

    @Override
    @Retry(name = "kafka-producer")
    public <T> void publish(TopicEnum topic, T payload, Throwable error) {
        String json;

        try {
            json = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(
                topic.value(),
                String.valueOf(generator.nextId()),
                new DeadLetterEvent<>(
                        generator.nextId(),
                        topic.value(),
                        error.getMessage(),
                        Instant.now(),
                        json
                )
        );
    }
}