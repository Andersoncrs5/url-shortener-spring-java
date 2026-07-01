package com.read.api.application.usecase.impl.deadLetterEvent;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.RetryDeadLetterEventUseCase;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RetryDeadLetterEventUseCaseImpl implements RetryDeadLetterEventUseCase {
    DeadLetterEventRepository repository;

    @Override
    public @NotNull Result<Void> execute(Long id) {


        return null;
    }

}
