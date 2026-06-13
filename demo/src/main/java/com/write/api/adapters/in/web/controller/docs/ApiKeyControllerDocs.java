package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseApiKey;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidString;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidationError;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.apiKey.ApiKeyDTO;
import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface ApiKeyControllerDocs {

    @ApiKey
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/test")
    @RateLimiter(name = "test")
    @Operation(
            summary = "",
            description = ""
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment updated successfully"
            ),
    })
    ResponseEntity<String> test(
            @RequestHeader("X-API-KEY") String key
    );

    @Idempotent
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "create")
    @Operation(
            summary = "Create API Key",
            description = """
                Creates a new API Key for a user.

                The generated key is returned only once in the response and
                cannot be recovered later because only its hash is stored.

                Requirements:
                - User must have ADMIN or SUPER_ADMIN role.
                - API key name must be unique.
                - Expiration date, when provided, must be in the future.
                - X-Idempotency-Key header is required to prevent duplicate requests.

                The generated API Key should be stored securely by the client.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "API Key created successfully",
                    content = @Content(
                            schema = @Schema(implementation = ResponseApiKey.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.
                        Possible reasons:
                        - Expiration date is in the past.
                        - Request body validation failed.
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
                        Access denied.
                        Only ADMIN or SUPER_ADMIN users can create API Keys.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        Referenced user not found.
                        Possible reasons:
                        - User does not exist.
                        - Owner user does not exist.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                        Conflict detected.
                        Possible reasons:
                        - API Key name already exists.
                        - Generated API Key hash already exists.
                        - Database unique constraint violation.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            )
    })
    ResponseEntity<ResponseHttp<String>> create(
            @RequestBody @Valid CreateApiKeyDTO dto,
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "update")
    @Operation(
            summary = "Update API Key",
            description = """
                Updates an existing API Key.

                The API Key must already exist.

                Requirements:
                - User must have ADMIN or SUPER_ADMIN role.
                - API Key name must remain unique.
                - Expiration date, when provided, must be in the future.
                - X-Idempotency-Key header is required to prevent duplicate updates.

                Only the provided fields will be updated.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "API Key updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = ResponseApiKey.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.
                        Possible reasons:
                        - Expiration date is in the past.
                        - Request body validation failed.
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
                        Access denied.
                        Only ADMIN or SUPER_ADMIN users can update API Keys.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        API Key not found.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                        Conflict detected.
                        Possible reasons:
                        - API Key name already exists.
                        - Database unique constraint violation.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            )
    })
    ResponseEntity<ResponseHttp<ApiKeyDTO>> update(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable
            @IsId
            Long id,

            @RequestBody @Valid UpdateApiKeyDTO dto,

            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate update requests",
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
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(
            summary = "Delete API Key",
            description = """
                Deletes an existing API Key.

                Requirements:
                - User must have ADMIN or SUPER_ADMIN role.
                - The API Key must exist.
                - X-Idempotency-Key header is required to prevent duplicate delete requests.

                This operation is irreversible.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "API Key deleted successfully",
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                        Access denied.
                        Only ADMIN or SUPER_ADMIN users can delete API Keys.
                        """,
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "API Key not found",
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ResponseHttp.class)
                    )
            )
    })
    ResponseEntity<ResponseHttp<Void>> delete(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable @IsId Long id,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate delete requests",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,

            @AuthenticationPrincipal UserPrincipal principal
    );

}
