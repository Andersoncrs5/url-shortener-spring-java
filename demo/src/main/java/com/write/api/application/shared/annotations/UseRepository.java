package com.write.api.application.shared.annotations;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repository
@Validated
public @interface UseRepository {
    String value() default "";
}
