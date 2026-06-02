package com.write.api.ports.in.apiKey;

import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface CreateApiKeyUseCase {
    Result<String> execute(CreateApiKeyDTO dto, @IsId Long userId);
}
