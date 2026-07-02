package com.read.api.api.controller.base.swagger.classes;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.url.UrlDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseHttpUrl {
    ResponseHTTP<UrlDTO> dto;
}
