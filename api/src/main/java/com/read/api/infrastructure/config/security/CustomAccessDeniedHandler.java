package com.read.api.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.api.dto.ResponseHTTP;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler
        implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AccessDeniedException exception
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ResponseHTTP<Void> body = ResponseHTTP.error(
                "You do not have permission to access this resource",
                getTraceId(request)
        );

        objectMapper.writeValue(
                response.getOutputStream(),
                body
        );
    }

    private String getTraceId(HttpServletRequest request) {
        Object traceId = request.getAttribute("traceId");

        return traceId != null
                ? traceId.toString()
                : UUID.randomUUID().toString();
    }
}