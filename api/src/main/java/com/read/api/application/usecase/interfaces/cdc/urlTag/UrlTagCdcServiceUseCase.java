package com.read.api.application.usecase.interfaces.cdc.urlTag;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlTagCdcEvent;

public interface UrlTagCdcServiceUseCase {
    void process(TiCdcEvent<UrlTagCdcEvent> event);
}
