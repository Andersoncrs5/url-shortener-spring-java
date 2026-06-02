package com.write.api.ports.in.apiKey;

import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface UpdateApiKeyUseCase {
    Result<ApiKeyModel> execute(UpdateApiKeyDTO dto, @IsId Long id, Long userId);
}
