package com.write.api.shared.validation.snowflake;

import com.write.api.shared.utils.SnowflakeUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SnowflakeValidator
        implements ConstraintValidator<IsId, Long> {

    @Override
    public boolean isValid(
            Long value,
            ConstraintValidatorContext context
    ) {
        return SnowflakeUtils.isValid(value);
    }
}