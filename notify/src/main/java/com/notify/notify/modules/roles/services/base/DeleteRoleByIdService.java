package com.notify.notify.modules.roles.services.base;

import com.notify.notify.globals.classes.result.Result;

public interface DeleteRoleByIdService {
    Result<Void> execute(Long id);
}
