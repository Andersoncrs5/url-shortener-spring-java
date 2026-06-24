package com.read.api.application.usecase.interfaces.cdc.urlAccessRule;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlAccessRuleCdcEvent;

public interface UrlAccessRuleCdcServiceUse {
    void process(TiCdcEvent<UrlAccessRuleCdcEvent> event);
}
