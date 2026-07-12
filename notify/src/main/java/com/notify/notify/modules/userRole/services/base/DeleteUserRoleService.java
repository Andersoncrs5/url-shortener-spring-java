package com.notify.notify.modules.userRole.services.base;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;

public interface DeleteUserRoleService {
    Result<Void> execute(Long id);
}
