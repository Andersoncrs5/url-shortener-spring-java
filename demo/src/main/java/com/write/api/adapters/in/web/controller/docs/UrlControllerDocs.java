package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.api.key.ApiKey;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    ResponseEntity<ResponseHttp<UrlResponseDTO>> createInternal(
            @RequestBody @Valid CreateUrlDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @RequestHeader("X-API-KEY") @NotBlank String key
    );

    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<UrlResponseDTO>> create(
            @RequestBody @Valid CreateUrlDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @DeleteMapping("/{id}")
    @RateLimiter(name = "delete")
    ResponseEntity<ResponseHttp<?>> delete(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @DeleteMapping("/{id}/force")
    @RateLimiter(name = "delete")
    ResponseEntity<ResponseHttp<?>> deleteForce(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @PatchMapping("/{id}")
    @RateLimiter(name = "update")
    ResponseEntity<ResponseHttp<?>> update(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

}
