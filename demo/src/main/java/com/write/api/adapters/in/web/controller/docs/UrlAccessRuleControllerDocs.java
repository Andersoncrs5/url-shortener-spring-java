package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseUrlAccessRule;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidString;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidationError;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UrlAccessRuleControllerDocs {
    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    @Operation(
            summary = "Create URL access rule",
            description = """
                Creates a new access control rule for a URL.

                Supported rule types:

                - MAX_CLICKS: Maximum number of allowed accesses.
                - RATE_LIMIT: Maximum requests allowed within a time window.
                - EXPIRES_AT: URL becomes inaccessible after a specific date.
                - COUNTRY_ALLOW: Allow access only from a specific country.
                - COUNTRY_BLOCK: Block access from a specific country.
                - IP_ALLOW: Allow access only from a specific IP address.
                - IP_BLOCK: Block access from a specific IP address.
                - USER_AGENT_BLOCK: Block requests matching a specific User-Agent.

                Requirements:
                - User must own the target URL.
                - Rule values must match the selected rule type.
                - Duplicate rules are not allowed.
                - X-Idempotency-Key header is required to prevent duplicate creation requests.

                A URL_ACCESS_RULE_CREATED event is published after successful creation.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "URL access rule created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrlAccessRule.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Invalid IP address.
                        - Invalid ISO country code.
                        - Invalid MAX_CLICKS value.
                        - Invalid RATE_LIMIT value.
                        - Empty USER_AGENT_BLOCK value.
                        - EXPIRES_AT date is missing.
                        - EXPIRES_AT date is in the past.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {
                                            ResponseValidString.class,
                                            ResponseValidationError.class,
                                    }
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                        Permission denied.

                        User does not own the target URL.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        Resource not found.

                        Possible reasons:
                        - URL not found.
                        - Assigned user not found.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                        Conflict detected.

                        An identical access rule already exists for the URL.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            )
    })
    ResponseEntity<ResponseHttp<?>> create(
            @RequestBody @Valid CreateUrlAccessRuleDTO dto,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @DeleteMapping("/{id}")
    @RateLimiter(name = "delete")
    @Operation(
            summary = "Delete URL access rule",
            description = """
                Deletes an existing URL access rule.

                Requirements:
                - URL access rule must exist.
                - X-Idempotency-Key header is required to prevent duplicate delete requests.

                This operation is irreversible.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL access rule deleted successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "URL access rule not found",
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {
                                            ResponseValidString.class,
                                            ResponseValidationError.class,
                                    }
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL access rule not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            )
    })
    ResponseEntity<ResponseHttp<?>> delete(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable
            @IsId
            Long id,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @PatchMapping("/{id}")
    @RateLimiter(name = "update")
    @Operation(
            summary = "Update URL access rule",
            description = """
                Updates an existing URL access rule.

                Only the fields provided in the request will be updated.

                Requirements:
                - URL access rule must exist.
                - X-Idempotency-Key header is required to prevent duplicate update requests.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL access rule updated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrlAccessRule.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.
                        Validation failed.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {
                                            ResponseValidString.class,
                                            ResponseValidationError.class,
                                    }
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL access rule not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            )
    })
    ResponseEntity<ResponseHttp<?>> update(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    example = "918273645"
            )
            @PathVariable
            @IsId
            Long id,
            @RequestBody @Valid UpdateUrlAccessRuleDTO dto,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

}
