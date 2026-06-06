package com.write.api.application.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.messaging.OutboxEventMessage;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.infrastructure.config.cache.RedisCrudService;
import com.write.api.ports.in.outbox.HandlerDlqUseCase;
import com.write.api.ports.out.messaging.OutboxEventPublisher;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HandlerDlqService implements HandlerDlqUseCase {

    IOutboxEventRepository repository;
    OutboxEventPublisher publisher;
    ObjectMapper objectMapper;
    RedisCrudService redisCrudService;

    @ResultTransaction
    @TrackExecutionTime("outbox.handler.dql")
    public <T> Result<Void> execute(
            String payload,
            TypeReference<OutboxEventMessage<T>> typeRef
    ) {
        OutboxEventMessage<T> event;

        try {
            event = objectMapper.readValue(payload, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize DLQ payload: {}", payload, e);
            throw new InternalServerErrorException(e.getMessage());
        }

        boolean firstExecution = redisCrudService.saveIfAbsent(
                "dlq:" + event.eventId(),
                "processed",
                Duration.ofHours(24)
        );

        if (!firstExecution) {
            log.info("Event {} already processed", event.eventId());
            return Result.success();
        }

        Optional<OutboxEventModel> optional = repository.findById(Long.valueOf(event.eventId()));

        if (optional.isEmpty()) {
            return Result.failure(404, "Event not found in outbox");
        }

        OutboxEventModel outboxEvent = optional.get();

        if (outboxEvent.getRetryCount() >= 20) {
            outboxEvent.setStatus(OutboxStatusEnum.FAILED);
            outboxEvent.setErrorMessage("Max retries exceeded");
            repository.save(outboxEvent);

            return Result.failure(400, "Max retries exceeded");
        }

        outboxEvent.setRetryCount(outboxEvent.getRetryCount() + 1);
        outboxEvent.setStatus(OutboxStatusEnum.RETRYING);

        repository.save(outboxEvent);

        publisher.publish(outboxEvent);

        return Result.success();
    }

}
