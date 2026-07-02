package com.read.api.application.usecase.interfaces.deadLetterEvent;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface FindDeadLetterEventByIdUseCase {
    Result<DeadLetterEventModel> execute(@IsId Long id);
}
