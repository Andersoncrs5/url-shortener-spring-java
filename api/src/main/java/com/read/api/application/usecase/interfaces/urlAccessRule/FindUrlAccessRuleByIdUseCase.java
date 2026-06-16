package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.utils.result.Result;

public interface FindUrlAccessRuleByIdUseCase {
    Result<UrlAccessRuleModel> execute(Long id);
}
