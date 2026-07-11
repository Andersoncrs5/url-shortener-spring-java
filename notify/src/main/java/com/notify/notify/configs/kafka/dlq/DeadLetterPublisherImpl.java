package com.notify.notify.configs.kafka.dlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.configs.snowflake.SnowflakeIdGenerator;
import com.notify.notify.globals.enums.TopicEnum;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
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
        String paylod;

        try {
            json = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        DeadLetterEvent<String> event = new DeadLetterEvent<>(
                generator.nextId(),
                topic.value(),
                error.getMessage(),
                Instant.now(),
                json
        );

        try {
            paylod = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(
                topic.value(),
                String.valueOf(generator.nextId()),
                paylod
        );
    }
}
