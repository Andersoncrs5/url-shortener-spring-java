package com.write.api.ports.in.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlModel;

public interface CreateUrlFromApiKeyUseCase {
    Result<UrlModel> execute(String key, CreateUrlDTO dto);
}
