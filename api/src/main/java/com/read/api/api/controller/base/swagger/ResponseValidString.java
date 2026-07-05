package com.read.api.api.controller.base.swagger;

import com.read.api.api.dto.ResponseHTTP;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseValidString {
    private ResponseHTTP<String> http;
}