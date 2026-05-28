package com.write.api.ports.in.urlRedirectRule;

import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteUrlRedirectRuleUseCase {
    Result<Void> execute(@IsId Long id);
}
