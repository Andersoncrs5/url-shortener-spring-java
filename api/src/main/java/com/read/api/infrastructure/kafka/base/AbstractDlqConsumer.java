package com.read.api.infrastructure.kafka.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.interfaces.deadLetterEvent.InsertDeadLetterEventUseCase;
import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.enums.TopicEnum;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.infrastructure.kafka.dlq.DeadLetterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDlqConsumer<T> {

    private final RedisCrudService cache;
    private final InsertDeadLetterEventUseCase insert;
    private final ObjectMapper mapper;

    protected void saveDeadLetter(
            ConsumerRecord<String, DeadLetterEvent<T>> record,
            TopicEnum sourceTopic,
            TopicEnum dlqTopic,
            String eventType
    ) {
        String eventId = record.key();
        String cacheKey = buildCacheKey(dlqTopic, eventId);
        String payload;

        cache.save(
                cacheKey,
                DeadLetterStatus.PENDING
        );

        DeadLetterStatus status =
                cache.find(cacheKey, DeadLetterStatus.class)
                        .orElse(null);

        if (status != null) {
            log.debug("DLQ event {} already processed", eventId);
            return;
        }

        DeadLetterEvent<T> event = record.value();

        try {
            payload = mapper.writeValueAsString(event.payload());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        DeadLetterEventModel model =
                DeadLetterEventModel.create(
                        Long.valueOf(eventId),
                        sourceTopic.value(),
                        dlqTopic.value(),
                        eventType,
                        payload,
                        20
                );

        var result = insert.execute(model);

        if (!result.isSuccess()) {

            log.error(
                    "Failed to persist DLQ event {}: {}",
                    eventId,
                    result.getMessage()
            );

            return;
        }

        cache.save(
                cacheKey,
                DeadLetterStatus.PROCESSING
        );

        log.info(
                "DLQ event {} persisted successfully",
                eventId
        );
    }

    protected String buildCacheKey(
            TopicEnum topic,
            String eventId
    ) {
        return "dlq:" + topic.value() + ":" + eventId;
    }
}
