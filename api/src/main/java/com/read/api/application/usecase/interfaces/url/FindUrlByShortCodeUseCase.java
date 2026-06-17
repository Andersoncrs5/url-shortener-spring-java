package com.read.api.application.usecase.interfaces.url;

import com.read.api.api.dto.url.AccessContextDTO;
import com.read.api.domain.model.UrlModel;
import com.read.api.utils.result.Result;

public interface FindUrlByShortCodeUseCase {
    Result<UrlModel> execute(String code, AccessContextDTO dto);
}
