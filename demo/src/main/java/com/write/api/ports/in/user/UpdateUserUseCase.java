package com.write.api.ports.in.user;

import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserModel;

public interface UpdateUserUseCase {
    Result<UserModel> update(UserModel user, UpdateUserDTO dto);
}
