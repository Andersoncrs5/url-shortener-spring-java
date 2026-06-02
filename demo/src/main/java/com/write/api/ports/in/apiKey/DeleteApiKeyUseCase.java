package com.write.api.ports.in.apiKey;

import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteApiKeyUseCase {
    Result<Void> execute(@IsId Long id, @IsId Long userId);
}
