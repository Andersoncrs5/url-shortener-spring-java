package com.read.api.application.usecase.interfaces.cdc.url;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;

public interface UrlCdcServiceUseCase {
    void process(TiCdcEvent<UrlCdcEvent> event);
}
