package com.write.api.adapters.in.web.controller.docs.swagger;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;

public record ResponseAuthTokens(
        ResponseHttp<AuthTokenResponseDTO> http
) {
}
