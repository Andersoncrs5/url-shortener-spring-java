package com.read.api.infrastructure.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import com.read.api.infrastructure.kafka.dlq.DeadLetterEvent;
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
public class DlqProducer {

    KafkaTemplate<String, Object> kafkaTemplate;
    SnowflakeIdGenerator generator;
    ObjectMapper mapper;

    @Retry(name = "kafka-producer")
    public <T> void publish(TopicEnum topic, T payload) {
        String json;

        try {
            json = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(
                topic.value(),
                String.valueOf(generator.nextId()),
                json
        );
    }

}
