package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.domain.model.UrlTagModel;
import com.read.api.utils.result.Result;

public interface SaveUrlTagUseCase {
    Result<UrlTagModel> execute(UrlTagModel model);
}
