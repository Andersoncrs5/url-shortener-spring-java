package com.read.api.api.controller.base.swagger.classes;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUrlRedirectRuleDTO {
    ResponseHTTP<UrlRedirectRuleDTO> dto;
}
