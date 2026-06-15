package com.read.api.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.api.dto.ResponseHTTP;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException exception
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        ResponseHTTP<Void> body = ResponseHTTP.error(
                "Authentication required",
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