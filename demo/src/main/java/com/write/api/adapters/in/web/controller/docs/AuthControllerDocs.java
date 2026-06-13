package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.controller.docs.swagger.ResponseAuthTokens;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidString;
import com.write.api.adapters.in.web.controller.docs.swagger.ResponseValidationError;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.validation.ValidationErrorResponse;
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
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface AuthControllerDocs {
    @Operation(
            summary = "Authenticate user",
            description = """
                Authenticates a user using email and password.

                On success, returns an access token and refresh token.

                Security rules:
                - Invalid credentials return 401.
                - After 3 consecutive failed login attempts, the account is blocked for 4 hours.
                - Blocked accounts return 423.
                - X-Idempotency-Key header is required to prevent duplicate requests.

                Successful and failed login attempts generate audit events.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseAuthTokens.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.
                        Possible reasons:
                        - Invalid email format.
                        - Missing required fields.
                        - Validation errors.
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
                    responseCode = "401",
                    description = """
                        Authentication failed.
                        Invalid email or password.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "423",
                    description = """
                        User account is temporarily blocked.

                        Accounts are blocked after 3 consecutive failed login attempts.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = """
                        Too many requests.
                        Rate limit exceeded.
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
    @PostMapping("/login")
    @RateLimiter(name = "login")
    ResponseEntity<ResponseHttp<AuthTokenResponseDTO>> login(
            @Valid @RequestBody LoginUserDTO dto,

            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate login requests",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

    @Operation(
            summary = "Register user",
            description = """
                Creates a new user account and immediately authenticates the user.

                On success, the API returns an access token and refresh token
                that can be used to access protected resources.

                Requirements:
                - Email must be unique.
                - Username must be unique (if applicable).
                - Password must meet validation requirements.
                - X-Idempotency-Key header is required to prevent duplicate registrations.

                A USER_CREATED event is published after successful registration.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseAuthTokens.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.

                        Possible reasons:
                        - Invalid email format.
                        - Weak password.
                        - Validation errors.
                        - Missing required fields.
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
                    responseCode = "409",
                    description = """
                        Conflict detected.

                        Possible reasons:
                        - Email already exists.
                        - Username already exists.
                        - Unique constraint violation.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = """
                        Too many requests.
                        Rate limit exceeded.
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
    @PostMapping("/register")
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<AuthTokenResponseDTO>> register(
            @Valid @RequestBody CreateUserDTO dto,

            @Parameter(
                    description = "Unique key used to guarantee idempotency and prevent duplicate registration requests",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

    @Idempotent
    @GetMapping("/logout")
    @RateLimiter(name = "logout")
    @Operation(
            summary = "Logout user",
            description = """
                Invalidates the current authenticated session.

                The user's refresh token is removed, preventing the generation
                of new access tokens.

                Requirements:
                - User must be authenticated.
                - X-Idempotency-Key header is required to prevent duplicate logout requests.

                This operation does not invalidate already issued access tokens.
                Access tokens remain valid until their expiration time.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout completed successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
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
    ResponseEntity<?> logout(
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Operation(
            summary = "Refresh authentication tokens",
            description = """
                Generates a new access token and refresh token using
                a valid refresh token.

                Security rules:
                - Refresh token must be valid and not expired.
                - Refresh token must belong to an existing user.
                - Refresh token must match the one currently stored for the user.
                - A new refresh token is issued on every successful request.

                The previous refresh token becomes invalid after rotation.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens refreshed successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseAuthTokens.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                        Invalid request data.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                        Invalid refresh token.

                        Possible reasons:
                        - Token expired.
                        - Token signature invalid.
                        - Token does not belong to the authenticated user.
                        - Token mismatch.
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
                        User associated with the refresh token was not found.
                        """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = ResponseHttp.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = """
                        Too many requests.
                        Rate limit exceeded.
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
    @GetMapping("/refresh-token/{refreshToken}")
    @RateLimiter(name = "refresh-token")
    ResponseEntity<ResponseHttp<?>> refreshToken(
            @PathVariable @NotBlank String refreshToken,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

}
