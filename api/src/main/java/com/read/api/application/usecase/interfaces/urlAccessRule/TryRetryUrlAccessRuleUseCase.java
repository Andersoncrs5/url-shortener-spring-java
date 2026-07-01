package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;

public interface TryRetryUrlAccessRuleUseCase {
    Result<Void> execute(DeadLetterEventModel event);
}
