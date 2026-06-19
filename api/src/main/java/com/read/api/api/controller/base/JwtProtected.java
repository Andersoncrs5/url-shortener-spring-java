package com.read.api.api.controller.base;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.*;

@Target({
        ElementType.TYPE,
        ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = "bearerAuth")
public @interface JwtProtected {
}