package com.notify.notify.modules.user.services.base;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.entities.UserEntity;

public interface FindUserByIdService {
    Result<UserEntity> execute(Long id);
}
