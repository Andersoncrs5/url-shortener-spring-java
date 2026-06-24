package com.read.api.application.usecase.interfaces.cdc.role;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;

public interface RoleCdcServiceUseCase {
    void process(TiCdcEvent<RoleCdcEvent> event);
}
