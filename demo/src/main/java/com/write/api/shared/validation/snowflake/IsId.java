package com.write.api.shared.validation.snowflake;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SnowflakeValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsId {

    String message() default "Invalid snowflake id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}