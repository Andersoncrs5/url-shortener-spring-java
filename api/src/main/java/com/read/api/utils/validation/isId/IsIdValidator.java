package com.read.api.utils.validation.isId;

import com.read.api.domain.utils.SnowflakeIdGenerator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IsIdValidator implements ConstraintValidator<IsId, Long> {

    @Override
    public boolean isValid(
            Long value,
            ConstraintValidatorContext context
    ) {
        return SnowflakeIdGenerator.isValid(value);
    }
}