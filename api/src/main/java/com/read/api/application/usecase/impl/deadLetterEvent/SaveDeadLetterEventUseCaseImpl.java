package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.SaveDeadLetterEventUseCase;
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
public class SaveDeadLetterEventUseCaseImpl implements SaveDeadLetterEventUseCase {
    DeadLetterEventRepository repository;

    @Override
    @Retry(name = "save")
    @ObservedMetric("dead.letter.event.save")
    public @NotNull Result<DeadLetterEventModel> execute(DeadLetterEventModel letter) {
        if (!repository.existsById(letter.getId())) {
            return Result.failure(
                    "Dead letter event not found",
                    404
            );
        }

        DeadLetterEventModel saved = repository.save(letter);


        return Result.success(saved, 201);
    }
}
