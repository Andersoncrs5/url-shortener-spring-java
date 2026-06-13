package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseUrlTagLink;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
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

public interface UrlTagLinkControllerDocs {

    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    @Operation(
            summary = "Link tag to URL",
            description = """
                Creates a relationship between a URL and a tag.

                Requirements:
                - User must be authenticated.
                - URL must exist.
                - Tag must exist.
                - Relationship must not already exist.
                - X-Idempotency-Key header is required to prevent duplicate requests.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Tag linked to URL successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrlTagLink.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        Resource not found.

                        Possible reasons:
                        - Url not found.
                        - Tag not found.
                        - User not found.
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
                        - Tag already linked to URL.
                        """,
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
    ResponseEntity<ResponseHttp<UrlTagLinkDTO>> create(
            @RequestBody @Valid CreateUrlTagLinkDTO dto,
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
            summary = "Delete URL tag link",
            description = """
                Removes the relationship between a URL and a tag.

                Requirements:
                - Link must exist.
                - X-Idempotency-Key header is required to prevent duplicate delete requests.

                This operation is irreversible.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag link deleted successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        Url Tag Link not found.
                        """,
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
    ResponseEntity<ResponseHttp<?>> del(
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
            summary = "Update URL tag link",
            description = """
                Updates an existing relationship between a URL and a tag.

                Requirements:
                - Link must exist.
                - URL must exist.
                - Tag must exist.
                - Updated relationship must remain unique.
                - X-Idempotency-Key header is required to prevent duplicate update requests.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tag link updated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseUrlTagLink.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                        Resource not found.

                        Possible reasons:
                        - Url Tag Link not found.
                        - Url not found.
                        - Tag not found.
                        - User not found.
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
                        - Tag already linked to URL.
                        """,
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
    ResponseEntity<ResponseHttp<UrlTagLinkDTO>> update(
            @Parameter(
                    description = "Unique URL Access Rule identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlTagLinkDTO dto,
             @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );
}
