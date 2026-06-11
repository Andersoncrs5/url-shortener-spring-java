package com.read.api.application.usecase.interfaces.url;

import com.read.api.utils.result.Result;

public interface DeleteUrlByIdUseCase {
    Result<Void> execute(Long id);
}
