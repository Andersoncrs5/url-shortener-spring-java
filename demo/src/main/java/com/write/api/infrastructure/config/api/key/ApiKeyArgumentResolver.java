package com.write.api.infrastructure.config.api.key;

import com.write.api.core.domain.exception.HttpException;
import com.write.api.ports.in.apiKey.ValidateApiKeyUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyArgumentResolver implements HandlerMethodArgumentResolver {

    ValidateApiKeyUseCase validateApiKey;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ApiKey.class)
                && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {

        String apiKey = webRequest.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new HttpException(
                    "API key is required",
                    HttpStatus.FORBIDDEN
            );
        }

        var result = validateApiKey.execute(apiKey);

        if (result.isFailure()) {
            throw new HttpException(
                    result.getMessage(),
                    HttpStatus.FORBIDDEN
            );
        }

        return apiKey;
    }
}