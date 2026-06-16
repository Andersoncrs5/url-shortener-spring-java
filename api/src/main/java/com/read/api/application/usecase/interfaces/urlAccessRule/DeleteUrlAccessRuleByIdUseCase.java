package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.utils.result.Result;

public interface DeleteUrlAccessRuleByIdUseCase {
    Result<Void> execute(Long id);
}
