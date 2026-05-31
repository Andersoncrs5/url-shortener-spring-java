package com.write.api.ports.in.urlAccessRule;

import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteUrlAccessRuleUseCase {
    Result<Void> execute(@IsId Long id);
}
