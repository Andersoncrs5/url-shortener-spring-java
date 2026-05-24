package com.write.api.ports.in.urlTag;

import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlTagModel;

public interface CreateUrlTagUseCase {
    Result<UrlTagModel> execute(CreateUrlTagDTO dto, Long userId);
}
