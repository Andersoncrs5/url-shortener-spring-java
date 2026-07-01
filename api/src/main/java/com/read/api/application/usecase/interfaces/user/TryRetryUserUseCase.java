package com.read.api.application.usecase.interfaces.user;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;

public interface TryRetryUserUseCase {
    Result<Void> execute(DeadLetterEventModel event);
}
