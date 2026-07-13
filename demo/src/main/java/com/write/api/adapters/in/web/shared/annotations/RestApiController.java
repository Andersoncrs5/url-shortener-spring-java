package com.write.api.adapters.in.web.shared.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
@RestController
@RequestMapping
public @interface RestApiController {

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "value"
    )
    String[] value() default {};

    @AliasFor(
            annotation = RequestMapping.class,
            attribute = "path"
    )
    String[] path() default {};
}