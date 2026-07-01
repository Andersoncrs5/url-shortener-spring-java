package com.read.api.application.usecase.interfaces.deadLetterEvent;

import com.read.api.utils.result.Result;

public interface RetryDeadLetterEventUseCase {
    Result<Void> execute(Long eventId);
}
