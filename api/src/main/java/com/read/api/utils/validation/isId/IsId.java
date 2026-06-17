package com.read.api.utils.validation.isId;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsIdValidator.class)
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
