package com.write.api.shared.validation.snowflake;

import com.write.api.shared.utils.SnowflakeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SnowflakeValidator implements ConstraintValidator<IsId, Object> {

    @Override
    public boolean isValid(
            Object value,
            ConstraintValidatorContext context
    ) {
        switch (value) {
            case null -> {
                return true;
            }
            case Long longValue -> {
                return SnowflakeUtils.isValid(longValue);
            }
            case String stringValue -> {
                try {
                    return SnowflakeUtils.isValid(Long.parseLong(stringValue));
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case Number numberValue -> {
                return SnowflakeUtils.isValid(numberValue.longValue());
            }
            default -> {
            }
        }

        // Se for qualquer outro tipo que não conseguimos converter para Long, a validação falha
        return false;
    }
}