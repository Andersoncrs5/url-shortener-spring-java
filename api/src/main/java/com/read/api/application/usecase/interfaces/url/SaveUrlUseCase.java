package com.read.api.application.usecase.interfaces.url;

import com.read.api.domain.model.UrlModel;
import com.read.api.utils.result.Result;

public interface SaveUrlUseCase {
    Result<UrlModel> execute(UrlModel url);
}
