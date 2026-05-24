package com.write.api.ports.in.urlTag;

import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlTagModel;

public interface UpdateUrlTagUseCase {
    Result<UrlTagModel> execute(Long id, UpdateUrlTagDTO dto);
}
