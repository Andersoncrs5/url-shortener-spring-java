package com.notify.notify.modules.user.services.base;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.dto.UserCdcEvent;
import com.notify.notify.modules.user.entities.UserEntity;

public interface SyncUserCdcService {
    Result<UserEntity> execute(TiCdcEvent<UserCdcEvent> event);
}
