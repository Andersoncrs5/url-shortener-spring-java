package com.notify.notify.modules.roles.services.base;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.dto.RoleCdcEvent;
import com.notify.notify.modules.roles.entities.RoleEntity;

public interface SyncRoleCdcService {
    Result<RoleEntity> execute(TiCdcEvent<RoleCdcEvent> entity) ;
}
