package com.read.api.api.controller.base;

public interface ConvertibleEnum {
    static <T extends Enum<T> & ConvertibleEnum> T fromString(Class<T> enumType, String value) {
        if (value == null) return null;

        String cleanedValue = value.toUpperCase();

        if (cleanedValue.endsWith("_ASC")) {
            cleanedValue = cleanedValue.substring(0, cleanedValue.length() - 4);
        } else if (cleanedValue.endsWith("_DESC")) {
            cleanedValue = cleanedValue.substring(0, cleanedValue.length() - 5);
        }

        try {
            return Enum.valueOf(enumType, cleanedValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para o enum " + enumType.getSimpleName() + ": " + value);
        }
    }
}