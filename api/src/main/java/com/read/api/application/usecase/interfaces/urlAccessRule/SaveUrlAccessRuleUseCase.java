package com.read.api.application.usecase.interfaces.urlAccessRule;

import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.utils.result.Result;

public interface SaveUrlAccessRuleUseCase {
    Result<UrlAccessRuleModel> execute(UrlAccessRuleModel model);
}
