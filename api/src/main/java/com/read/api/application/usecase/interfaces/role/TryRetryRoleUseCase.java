package com.read.api.application.usecase.interfaces.role;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;

public interface TryRetryRoleUseCase {
    Result<Void> execute(DeadLetterEventModel event);
}
