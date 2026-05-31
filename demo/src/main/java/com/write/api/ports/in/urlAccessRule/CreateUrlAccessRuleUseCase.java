package com.write.api.ports.in.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface CreateUrlAccessRuleUseCase {
    Result<UrlAccessRuleModel> execute(CreateUrlAccessRuleDTO dto, @IsId Long assignedByUserId);
}
