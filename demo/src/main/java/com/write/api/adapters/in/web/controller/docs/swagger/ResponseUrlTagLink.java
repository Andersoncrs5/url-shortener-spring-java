package com.write.api.adapters.in.web.controller.docs.swagger;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;

public record ResponseUrlTagLink(
        ResponseHttp<UrlTagLinkDTO> http
) {
}
