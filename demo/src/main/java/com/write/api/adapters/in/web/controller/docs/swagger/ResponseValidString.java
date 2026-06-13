package com.write.api.adapters.in.web.controller.docs.swagger;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;

public record ResponseValidString(
        ResponseHttp<String> http
) {
}
