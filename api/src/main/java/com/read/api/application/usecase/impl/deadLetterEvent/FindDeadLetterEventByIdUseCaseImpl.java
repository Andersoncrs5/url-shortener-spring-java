package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.FindDeadLetterEventByIdUseCase;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.infrastructure.tx.ResultTransaction;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindDeadLetterEventByIdUseCaseImpl implements FindDeadLetterEventByIdUseCase {

    DeadLetterEventRepository repository;

    @Override
    @Retry(name = "read")
    @ResultTransaction(readOnly = true)
    @ObservedMetric("dead.letter.event.find.id")
    public Result<DeadLetterEventModel> execute(Long id) {
        return repository.findById(id)
                .map(Result::success)
                .orElseGet(
                        () -> Result.failure("Dead Letter Event not found", 404)
                );
    }
}
