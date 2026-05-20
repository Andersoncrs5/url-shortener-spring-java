package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.config.api.idempotent.Idempotent;
import com.write.api.config.security.classes.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface AuthControllerDocs {
    @PostMapping("/login")
    ResponseEntity<ResponseHttp<AuthTokenResponseDTO>> login(
            @Valid @RequestBody LoginUserDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @PostMapping("/register")
    ResponseEntity<ResponseHttp<AuthTokenResponseDTO>> register(
            @Valid @RequestBody CreateUserDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @GetMapping("/logout")
    ResponseEntity<?> logout(
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @GetMapping("/refresh-token/{refreshToken}")
    ResponseEntity<ResponseHttp<?>> refreshToken(
            @PathVariable @NotBlank String refreshToken,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

}
