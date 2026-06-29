package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.DeleteDeadLetterEventByIdUseCase;
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
public class DeleteDeadLetterEventByIdUseCaseImpl implements DeleteDeadLetterEventByIdUseCase {
    DeadLetterEventRepository repository;

    @Override
    @Retry(name = "delete")
    @ObservedMetric("dead.letter.event.delete")
    public @NotNull Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Dead Letter Event not found");
        }

        return Result.success();
    }

}
