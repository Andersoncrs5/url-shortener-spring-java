package com.read.api.api.controller.base;

import com.read.api.api.controller.swagger.ResponseValidString;
import com.read.api.api.dto.ResponseHTTP;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
                schema = @Schema(
                        oneOf = {
                                ResponseValidString.class
                        }
                )
        )
)
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(
                schema = @Schema(
                        oneOf = {
                                ResponseHTTP.class
                        }
                )
        )
)
@ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(
                schema = @Schema(
                        oneOf = {
                                ResponseHTTP.class
                        }
                )
        )
)
@ApiResponse(
        responseCode = "404",
        description = "Resource not found",
        content = @Content(
                schema = @Schema(
                        oneOf = {
                                ResponseHTTP.class
                        }
                )
        )
)
@ApiResponse(
        responseCode = "500",
        description = "Internal Server Error",
        content = @Content(
                schema = @Schema(
                        oneOf = {
                                ResponseHTTP.class
                        }
                )
        )
)
public @interface DefaultApiResponses {
}