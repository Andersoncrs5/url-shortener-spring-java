package com.write.api.ports.in.auth;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;

public interface LogoutAuthUseCase {
    Result<UserModel> execute(Long id);
}
