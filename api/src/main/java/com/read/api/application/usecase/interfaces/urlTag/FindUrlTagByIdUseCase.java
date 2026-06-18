package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.domain.model.UrlTagModel;
import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface FindUrlTagByIdUseCase {
    Result<UrlTagModel> execute(@IsId Long id);
}
