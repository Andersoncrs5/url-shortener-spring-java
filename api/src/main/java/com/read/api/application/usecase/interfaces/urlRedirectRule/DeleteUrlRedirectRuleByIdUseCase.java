package com.read.api.application.usecase.interfaces.urlRedirectRule;

import com.read.api.utils.result.Result;

public interface DeleteUrlRedirectRuleByIdUseCase {
    Result<Void> execute(Long id);
}
