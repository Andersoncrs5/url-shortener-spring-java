package com.read.api.application.usecase.interfaces.role;

import com.read.api.domain.model.RoleModel;
import com.read.api.utils.result.Result;

public interface FindRoleByIdUseCase {
    Result<RoleModel> execute(Long id);
}
