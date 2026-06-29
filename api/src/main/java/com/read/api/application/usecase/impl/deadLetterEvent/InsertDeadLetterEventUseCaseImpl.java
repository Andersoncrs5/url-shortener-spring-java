package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.InsertDeadLetterEventUseCase;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertDeadLetterEventUseCaseImpl implements InsertDeadLetterEventUseCase {
    DeadLetterEventRepository repository;

    @Override
    @Retry(name = "insert")
    @ObservedMetric("dead.letter.event.insert")
    public @NotNull Result<DeadLetterEventModel> execute(DeadLetterEventModel letter) {
        DeadLetterEventModel inserted = repository.insert(letter);

        return Result.success(inserted, 201);
    }
}
