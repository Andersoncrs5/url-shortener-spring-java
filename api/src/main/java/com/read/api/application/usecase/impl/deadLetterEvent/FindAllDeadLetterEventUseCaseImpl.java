package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.FindAllDeadLetterEventUseCase;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllDeadLetterEventUseCaseImpl implements FindAllDeadLetterEventUseCase {
    DeadLetterEventRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("dead.letter.event.find.all.filter")
    public @NotNull Page<DeadLetterEventModel> execute(DeadLetterEventFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }

}
