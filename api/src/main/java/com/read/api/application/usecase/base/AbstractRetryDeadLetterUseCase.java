package com.read.api.application.usecase.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.domain.cdc.BaseCdcEvent;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

public abstract class AbstractRetryDeadLetterUseCase<
        TCdc extends BaseCdcEvent,
        TModel> {

    protected final DeadLetterEventRepository eventRepository;
    protected final ObjectMapper objectMapper;

    protected AbstractRetryDeadLetterUseCase(
            DeadLetterEventRepository eventRepository,
            ObjectMapper objectMapper
    ) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    protected abstract TypeReference<TiCdcEvent<TCdc>> type();

    protected abstract TModel toModel(TCdc event);

    protected abstract Result<?> insert(TModel model);

    protected abstract Result<?> save(TModel model);

    protected abstract Result<?> delete(Long id);

    public Result<Void> execute(DeadLetterEventModel event) {

        if (event == null) {
            return Result.failure("Event not found",404);
        }

        TiCdcEvent<TCdc> response;

        try {

            response = objectMapper.readValue(
                    event.getPayload(),
                    type()
            );

        } catch (JsonProcessingException ex) {

            event.markAsFailed();
            event.setErrorMessage(ex.getMessage());

            eventRepository.save(event);

            return Result.failure("Invalid payload",500);
        }

        Result<?> result;



        if (response.isDelete()) {
            result = delete(
                    (response.firstData()).id()
            );

        } else if (response.isInsert()) {

            result = insert(
                    toModel(response.firstData())
            );

        } else if (response.isUpdate()) {

            result = save(
                    toModel(response.firstData())
            );

        } else {

            return Result.failure(
                    "Unknown event",
                    400
            );
        }

        if (result.isFailure()) {

            event.incrementRetry();
            event.setErrorMessage(result.getMessage());

            eventRepository.save(event);

            return Result.failure(
                    result.getStatusCode(),
                    result.getMessage()
            );
        }

        event.markAsResolved();

        eventRepository.save(event);

        return Result.success();
    }
}