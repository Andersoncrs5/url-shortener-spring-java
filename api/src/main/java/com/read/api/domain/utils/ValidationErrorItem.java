package com.read.api.domain.utils;

public record ValidationErrorItem(
        String code,
        String message
) {
}