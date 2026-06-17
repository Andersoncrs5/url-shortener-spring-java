package com.read.api.application.usecase.interfaces.url;

import com.read.api.domain.model.UrlModel;
import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface FindUrlByIdUseCase {
    Result<UrlModel> execute(@IsId Long id);
}
