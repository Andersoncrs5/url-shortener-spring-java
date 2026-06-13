package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseUrl;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.api.key.ApiKey;
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

public interface UrlControllerDocs {

    @ApiKey
    @Idempotent
    @PostMapping("/internal")
    @RateLimiter(name = "create-internal")
    @Operation(
            summary = "Create short URL using API Key",
            description = """
                Creates a new shortened URL using an API Key.

                This endpoint is intended for system-to-system integrations,
                external applications, automation workflows, and third-party services.

                Authentication:
                - A valid API Key must be provided in the X-API-KEY header.
                - The API Key must be active.
                - The API Key must not be expired.

                Features:
                - Automatically generates a unique short code.
                - Supports password-protected URLs.
                - Creates the URL under the API Key owner's account.
                - Publishes a URL_CREATED event after successful creation.

                The X-Idempotency-Key header is required to prevent duplicate URL creation.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Short URL created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrl.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Missing required fields.
                        - Invalid URL format.
                        - Field size exceeded.
                        - Validation errors.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                        API Key validation failed.

                        Possible reasons:
                        - Invalid API Key.
                        - Inactive API Key.
                        - Expired API Key.
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
                        - User associated with the API Key not found.
                        - API Key not found.
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
                        - API Key disabled.
                        - API Key expired.
                        - Generated short code collision.
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
    ResponseEntity<ResponseHttp<UrlResponseDTO>> createInternal(
            @RequestBody @Valid CreateUrlDTO dto,
             @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @RequestHeader("X-API-KEY") @NotBlank String key
    );

    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    @Operation(
            summary = "Create short URL",
            description = """
                Creates a new shortened URL for the authenticated user.

                Features:
                - Automatically generates a unique short code.
                - Associates the URL with the authenticated user.
                - Supports password-protected URLs.
                - URLs are created with ACTIVE status by default.
                - Publishes a URL_CREATED event after successful creation.

                Authentication:
                - Requires a valid JWT access token.

                The X-Idempotency-Key header is required to prevent duplicate URL creation.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Short URL created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrl.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Missing required fields.
                        - Invalid URL format.
                        - Field exceeded maximum allowed length.
                        - Validation errors.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or invalid JWT token",
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
                        - Authenticated user not found.
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
                        - Generated short code already exists.
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
    ResponseEntity<ResponseHttp<UrlResponseDTO>> create(
            @RequestBody @Valid CreateUrlDTO dto,
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
            summary = "Delete URL",
            description = """
                Soft deletes an existing URL.

                The URL is not physically removed from the database.
                Instead, its status is changed to DELETED and a deletion timestamp is recorded.

                Requirements:
                - URL must exist.
                - X-Idempotency-Key header is required to prevent duplicate delete requests.

                This operation makes the URL unavailable for future access.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL deleted successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
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
    @DeleteMapping("/{id}/force")
    @RateLimiter(name = "delete")
    @Operation(
            summary = "Permanently delete URL",
            description = """
                Permanently deletes an existing URL.

                Unlike the standard delete endpoint, this operation physically
                removes the URL from the database and cannot be undone.

                Requirements:
                - URL must exist.
                - X-Idempotency-Key header is required to prevent duplicate delete requests.

                A URL_DELETED event is published before the URL is removed.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL permanently deleted successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL not found",
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
    ResponseEntity<ResponseHttp<?>> deleteForce(
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
            summary = "Update URL",
            description = """
                Updates an existing URL.

                Supported updates may include:
                - Title
                - Original URL
                - Status
                - Password protection settings
                - Other mutable URL attributes

                Features:
                - Preserves the existing short code.
                - Supports updating password-protected URLs.
                - Publishes a URL_UPDATED event after successful update.

                Requirements:
                - URL must exist.
                - X-Idempotency-Key header is required to prevent duplicate update requests.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL updated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrl.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Missing required fields.
                        - Invalid field values.
                        - Field exceeded maximum allowed length.
                        - Validation errors.
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
                        - Referenced user not found.
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
                        - Short code already exists.
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
    ResponseEntity<ResponseHttp<?>> update(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlDTO dto,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

}
