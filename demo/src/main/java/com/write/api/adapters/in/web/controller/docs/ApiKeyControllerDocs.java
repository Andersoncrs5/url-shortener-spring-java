package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.apiKey.ApiKeyDTO;
import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.api.key.ApiKey;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    ResponseEntity<String> test(
            @RequestHeader("X-API-KEY") String key
    );

    @Idempotent
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<String>> create(
            @RequestBody @Valid CreateApiKeyDTO dto,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "update")
    ResponseEntity<ResponseHttp<ApiKeyDTO>> update(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateApiKeyDTO dto,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @DeleteMapping("/{id}")
    @RateLimiter(name = "delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    ResponseEntity<ResponseHttp<Void>> delete(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

}
