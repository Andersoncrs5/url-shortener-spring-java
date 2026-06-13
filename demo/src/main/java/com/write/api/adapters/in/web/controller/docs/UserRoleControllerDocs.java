package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseUserRole;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidString;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidationError;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UserRoleControllerDocs {

    @Operation(
            summary = "Assign role to user",
            description = "Assign a role to a user. Only ADMIN and SUPER_ADMIN can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Role assigned successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ResponseUserRole.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
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
                    description = "User or role not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already has this role or operation is not allowed"
            )
    })
    @Idempotent
    @PostMapping("/add-role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<?>> create(
            @Valid @RequestBody CreateUserRoleDTO dto,
            @Parameter(
                    description = "Unique key used to guarantee idempotency",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Operation(
            summary = "Remove role from user",
            description = "Remove a role from a user. Only ADMIN and SUPER_ADMIN can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Role removed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {
                                            ResponseValidString.class,
                                    }
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User role, user or role not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Operation is not allowed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Failed to remove user role"
            )
    })
    @Idempotent
    @DeleteMapping("/remove-role/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "delete")
    ResponseEntity<ResponseHttp<?>> delete(
            @Parameter(
                    description = "Unique User Role identifier",
                    required = true,
                    example = "918273645"
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Unique key used to guarantee idempotency",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );
}