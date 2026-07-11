package com.notify.notify.modules.user.services.base;

import com.notify.notify.globals.classes.result.Result;

public interface DeleteUserByIdService {
    Result<Void> execute(Long id);
}
