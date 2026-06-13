package com.write.api.adapters.in.web.controller.docs.swagger;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;

public record ResponseUrlRedirectRule(
        ResponseHttp<UrlRedirectRuleDTO> dtoResponseHttp
) {
}
