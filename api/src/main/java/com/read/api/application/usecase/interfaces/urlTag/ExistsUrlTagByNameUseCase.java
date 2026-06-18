package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.utils.result.Result;

public interface ExistsUrlTagByNameUseCase {
    Result<Boolean> execute(String name);
}
