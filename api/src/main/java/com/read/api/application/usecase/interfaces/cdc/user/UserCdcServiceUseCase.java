package com.read.api.application.usecase.interfaces.cdc.user;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;

public interface UserCdcServiceUseCase {
    void process(TiCdcEvent<UserCdcEvent> event);
}
