package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.utils.result.Result;

public interface ExistsUrlTagBySlugUseCase {
    Result<Boolean> execute(String slug);
}
