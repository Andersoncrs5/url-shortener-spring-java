package com.read.api.application.usecase.interfaces.cdc.urlRedirectRule;

import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlRedirectRuleCdcEvent;

public interface UrlRedirectRuleCdcServiceUseCase {
    void process(TiCdcEvent<UrlRedirectRuleCdcEvent> event);

}
