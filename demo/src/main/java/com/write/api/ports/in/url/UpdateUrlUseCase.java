package com.write.api.ports.in.url;

import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface UpdateUrlUseCase {
    Result<UrlModel> execute(@IsId Long id, UpdateUrlDTO dto);
}
