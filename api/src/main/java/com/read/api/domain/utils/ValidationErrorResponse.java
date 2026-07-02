package com.read.api.domain.utils;

import java.util.List;
import java.util.Map;

public record ValidationErrorResponse(
        boolean status,
        String message,
        Map<String, List<ValidationErrorItem>> errors,
        String traceId
) {
}