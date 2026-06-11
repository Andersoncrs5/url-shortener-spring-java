package com.read.api.application.usecase.interfaces.role;

import com.read.api.utils.result.Result;

public interface DeleteRoleByIdUseCase {
    Result<Void> execute(Long id);
}
