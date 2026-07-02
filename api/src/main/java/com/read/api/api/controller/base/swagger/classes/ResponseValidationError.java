package com.read.api.api.controller.base.swagger.classes;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.domain.utils.ValidationErrorResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseValidationError {
    ResponseHTTP<ValidationErrorResponse> http;
}
