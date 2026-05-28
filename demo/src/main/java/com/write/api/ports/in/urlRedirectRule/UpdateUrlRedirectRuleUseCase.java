package com.write.api.ports.in.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface UpdateUrlRedirectRuleUseCase {
    Result<UrlRedirectRuleModel> execute(@IsId Long id, UpdateUrlRedirectRuleDTO dto);
}
