package com.write.api.application.service.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.apiKey.FindByKeyApiKeyUseCase;
import com.write.api.ports.in.apiKey.ValidateApiKeyUseCase;
import com.write.api.ports.in.url.CreateUrlFromApiKeyUseCase;
import com.write.api.ports.in.url.CreateUrlUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUrlFromApiKeyService implements CreateUrlFromApiKeyUseCase {

    CreateUrlUseCase useCase;
    ValidateApiKeyUseCase validateApiKey;
    FindByKeyApiKeyUseCase findByKeyApiKey;

    @Override
    public Result<UrlModel> execute(String key, CreateUrlDTO dto) {
        ApiKeyModel apiKey;

        Result<Boolean> booleanResult = validateApiKey.execute(key);
        if (booleanResult.isFailure()) {
            return Result.failure(booleanResult.getErrors(), booleanResult.getStatusCode());
        }

        Result<ApiKeyModel> apiKeyResult = findByKeyApiKey.execute(key);
        if (apiKeyResult.isFailure()) {
            return Result.failure(apiKeyResult.getStatusCode(), apiKeyResult.getMessage());
        }

        apiKey = apiKeyResult.getValue();

        if (!apiKey.isActive()) {
            return Result.failure(409, "Api key is disabled");
        }
        if (apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            return Result.failure(409, "Api key is expired");
        }

        return useCase.execute(dto, apiKey.getOwnerUserId());
    }
}
