package com.notify.notify.modules.userRole.services.base;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.userRole.dto.UserRoleCdcEvent;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;

public interface SyncUserRoleCdcService {
    Result<UserRoleEntity> execute(TiCdcEvent<UserRoleCdcEvent> event);
}
