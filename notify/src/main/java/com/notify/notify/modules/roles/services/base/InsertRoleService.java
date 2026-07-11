package com.notify.notify.modules.roles.services.base;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;

public interface InsertRoleService {
    Result<RoleEntity> execute(RoleEntity role);
}
