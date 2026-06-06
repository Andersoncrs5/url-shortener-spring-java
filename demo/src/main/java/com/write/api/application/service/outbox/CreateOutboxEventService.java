package com.write.api.application.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateOutboxEventService implements CreateOutboxEventUseCase {

    IOutboxEventRepository repository;
    ObjectMapper objectMapper;

    @Override
    @ResultTransaction
    @TrackExecutionTime("outbox.create.event")
    public Result<OutboxEventModel> execute(CreateOutboxEventCommand command) {

        try {
            String payload = objectMapper.writeValueAsString(command.payload());

            OutboxEventModel model = new OutboxEventModel();

            model.setAggregateType(command.aggregateType());
            model.setAggregateId(command.aggregateId());
            model.setEventType(command.eventType());
            model.setTopic(command.topic());
            model.setPayload(payload);
            model.setStatus(OutboxStatusEnum.PENDING);
            model.setRetryCount(0);

            OutboxEventModel inserted = repository.insert(model);

            return Result.success(inserted, 201);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox payload", e);

            return Result.failure(
                    500,
                    "Failed to serialize outbox payload"
            );
        }
    }

}
