package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;

public interface TryRetryUrlTagUseCase {
    Result<Void> execute(DeadLetterEventModel event);
}
