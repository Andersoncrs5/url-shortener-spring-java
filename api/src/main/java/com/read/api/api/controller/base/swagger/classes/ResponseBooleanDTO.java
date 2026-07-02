package com.read.api.api.controller.base.swagger.classes;

import com.read.api.api.dto.ResponseHTTP;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBooleanDTO {
    ResponseHTTP<Boolean> result;
}