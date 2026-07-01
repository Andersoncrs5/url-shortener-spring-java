package com.read.api.application.usecase.interfaces.urlRedirectRule;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.result.Result;

public interface TryRetryUrlRedirectRuleUseCase {
    Result<Void> execute(DeadLetterEventModel event);
}
