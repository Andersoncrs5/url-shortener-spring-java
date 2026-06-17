package com.read.api.application.usecase.interfaces.urlRedirectRule;

import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.utils.result.Result;

public interface SaveUrlRedirectRuleUseCase {
    Result<UrlRedirectRuleModel> execute(UrlRedirectRuleModel model);
}
