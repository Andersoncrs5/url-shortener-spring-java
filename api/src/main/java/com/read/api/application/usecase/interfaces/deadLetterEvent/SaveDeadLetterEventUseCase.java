package com.read.api.application.usecase.interfaces.deadLetterEvent;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;
import jakarta.validation.constraints.NotNull;

public interface SaveDeadLetterEventUseCase {
    Result<DeadLetterEventModel> execute(@NotNull DeadLetterEventModel letter);
}
