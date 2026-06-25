package com.read.api.application.usecase.interfaces.deadLetterEvent;

import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface DeleteDeadLetterEventByIdUseCase {
    Result<Void> execute(@IsId Long id);
}
