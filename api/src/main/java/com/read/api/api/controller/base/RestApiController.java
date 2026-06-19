package com.read.api.api.controller.base;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "API")
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