package com.write.api.ports.in.user;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;

public interface CreateUserUseCase {
    Result<UserModel> create(UserModel user);
}
