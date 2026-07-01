package com.read.api.application.usecase.interfaces.url;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;

public interface TryRetryUrlUseCase {
    Result<Void> execute(DeadLetterEventModel event);
}
