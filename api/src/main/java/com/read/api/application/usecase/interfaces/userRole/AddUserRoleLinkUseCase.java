package com.read.api.application.usecase.interfaces.userRole;

import com.read.api.domain.model.UserModel;
import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface AddUserRoleLinkUseCase {
    Result<UserModel> execute(@IsId Long userId, @IsId Long roleId);
}
