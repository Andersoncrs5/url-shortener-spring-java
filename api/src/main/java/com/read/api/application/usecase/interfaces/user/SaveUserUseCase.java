package com.read.api.application.usecase.interfaces.user;

import com.read.api.domain.model.UserModel;
import com.read.api.utils.result.Result;

public interface SaveUserUseCase {
    Result<UserModel> execute(UserModel user);
}
