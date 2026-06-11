package com.read.api.application.usecase.interfaces.user;

import com.read.api.utils.result.Result;

public interface DeleteByIdUserUseCase {
    Result<Void> execute(Long id);
}
