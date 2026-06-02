package com.write.api.ports.in.apiKey;

import com.write.api.application.shared.Result;

public interface ValidateApiKeyUseCase {
    Result<Boolean> execute(String key);
}
