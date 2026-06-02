package com.write.api.ports.in.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface UpdateUrlAccessRuleUseCase {
    Result<UrlAccessRuleModel> execute(UpdateUrlAccessRuleDTO dto, @IsId Long id);
}
