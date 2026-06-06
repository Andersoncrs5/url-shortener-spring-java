package com.write.api.ports.in.apiKey;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.ApiKeyModel;
import jakarta.validation.constraints.NotBlank;

public interface FindByKeyApiKeyUseCase {
    Result<ApiKeyModel> execute(@NotBlank String key);
}
