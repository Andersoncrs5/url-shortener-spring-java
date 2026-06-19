package com.read.api.api.controller.swagger;

import com.read.api.api.dto.ResponseHTTP;

public record ResponseValidString(
        ResponseHTTP<String> http
) {
}