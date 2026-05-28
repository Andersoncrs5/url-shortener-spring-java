package com.write.api.ports.in.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlRedirectRuleModel;

public interface CreateUrlRedirectRuleUseCase {
    Result<UrlRedirectRuleModel> execute(CreateUrlRedirectRuleDTO dto);
}
