package com.write.api.application.shared.validation;

public record ValidationErrorItem(
        String code,
        String message
) {
}