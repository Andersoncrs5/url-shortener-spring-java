package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseUrlTag;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


public interface UrlTagControllerDocs {

    @Operation(
            summary = "Create URL tag",
            description = """
                    Creates a new tag for the authenticated user.

                    Requirements:
                    - User must be authenticated.
                    - Tag name must be unique.
                    - Tag slug must be unique.
                    - If parentId is provided, the parent tag must exist.
                    - X-Idempotency-Key header is required to prevent duplicate requests.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUrlTag.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request data.
                            Possible reasons:
                            - Validation errors.
                            - Database integrity error.
                            """,
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            Parent tag not found.
                            """,
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            Conflict detected.
                            Possible reasons:
                            - Slug already exists.
                            - Name already exists.
                            """,
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<UrlTagResponseDTO>> create(
            @RequestBody @Valid CreateUrlTagDTO dto,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate tag creation requests",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Operation(
            summary = "Delete URL tag",
            description = """
                    Deletes an existing tag.

                    Requirements:
                    - Tag must exist.
                    - X-Idempotency-Key header is required to prevent duplicate delete requests.

                    This operation is irreversible.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Tag not found",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    @Idempotent
    @DeleteMapping("/{id}")
    @RateLimiter(name = "delete")
    ResponseEntity<ResponseHttp<?>> del(
            @Parameter(
                    description = "Unique URL tag identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable @IsId Long id,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate tag deletion requests",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

    @Operation(
            summary = "Update URL tag",
            description = """
                    Updates an existing tag.

                    Rules:
                    - Tag must exist.
                    - If parentId is provided, it cannot point to the same tag.
                    - If parentId is provided, the parent tag must exist.
                    - Tag name must remain unique.
                    - Tag slug must remain unique.
                    - X-Idempotency-Key header is required to prevent duplicate update requests.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUrlTag.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request data.
                            Possible reasons:
                            - Validation errors.
                            - Database integrity error.
                            """,
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            Resource not found.
                            Possible reasons:
                            - Tag not found.
                            - Parent tag not found.
                            """,
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            Conflict detected.
                            Possible reasons:
                            - Slug already exists.
                            - Name already exists.
                            - Parent id conflict.
                            """,
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected internal server error",
                    content = @Content(schema = @Schema(implementation = ResponseHttp.class))
            )
    })
    @Idempotent
    @PatchMapping("/{id}")
    @RateLimiter(name = "update")
    ResponseEntity<ResponseHttp<UrlTagResponseDTO>> update(
            @Parameter(
                    description = "Unique URL tag identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlTagDTO dto,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate tag update requests",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );
}