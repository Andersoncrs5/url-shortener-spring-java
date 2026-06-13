package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseUrlRedirectRule;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
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
import org.springframework.web.bind.annotation.*;

public interface UrlRedirectRuleControllerDocs {

    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    @Operation(
            summary = "Create URL redirect rule",
            description = """
                Creates a redirect rule for a shortened URL.

                Redirect rules allow traffic routing based on request attributes.

                Supported matching criteria may include:
                - Country
                - Region
                - Continent
                - Operating System
                - Browser
                - Match Type
                - Priority

                Features:
                - Generates a unique rule hash to prevent duplicates.
                - Multiple rules can be configured for the same URL.
                - Priority determines evaluation order.

                The X-Idempotency-Key header is required to prevent duplicate rule creation requests.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Redirect rule created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrlRedirectRule.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Validation errors.
                        - Invalid redirect configuration.
                        - Missing required fields.
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

                        Possible reasons:
                        - An identical redirect rule already exists for this URL.
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
    ResponseEntity<ResponseHttp<UrlRedirectRuleDTO>> create(
            @RequestBody @Valid CreateUrlRedirectRuleDTO dto,
             @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

    @Idempotent
    @DeleteMapping("/{id}")
    @RateLimiter(name = "delete")
    @Operation(
            summary = "Delete URL redirect rule",
            description = """
                Deletes an existing URL redirect rule.

                Redirect rules are used to control URL routing based on
                matching conditions such as country, region, browser,
                operating system, and other supported criteria.

                Requirements:
                - Redirect rule must exist.
                - X-Idempotency-Key header is required to prevent duplicate delete requests.

                This operation is irreversible.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Redirect rule deleted successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Redirect rule not found",
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
            @PathVariable @IsId Long id,
             @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

    @Idempotent
    @PatchMapping("/{id}")
    @RateLimiter(name = "update")
    @Operation(
            summary = "Update URL redirect rule",
            description = """
                Updates an existing URL redirect rule.

                Redirect rules control URL routing based on request attributes such as:
                - Country
                - Region
                - Continent
                - Browser
                - Operating System
                - Match Type
                - Priority

                Features:
                - Recalculates the rule hash after updates.
                - Prevents duplicate redirect rules for the same URL.
                - Preserves the existing rule identifier.

                Requirements:
                - Redirect rule must exist.
                - X-Idempotency-Key header is required to prevent duplicate update requests.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Redirect rule updated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrlRedirectRule.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Validation errors.
                        - Invalid redirect configuration.
                        - Missing required fields.
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
                        - Redirect rule not found.
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

                        Possible reasons:
                        - An identical redirect rule already exists for the URL.
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
    ResponseEntity<ResponseHttp<UrlRedirectRuleDTO>> update(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlRedirectRuleDTO dto,
             @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

}
