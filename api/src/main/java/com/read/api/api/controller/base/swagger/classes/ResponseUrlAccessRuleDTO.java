package com.read.api.api.controller.base.swagger.classes;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUrlAccessRuleDTO {
    ResponseHTTP<UrlAccessRuleDTO> dto;
}
