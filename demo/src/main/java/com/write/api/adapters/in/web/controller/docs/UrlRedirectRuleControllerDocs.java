package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.shared.validation.snowflake.IsId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UrlRedirectRuleControllerDocs {

    @Idempotent
    @PostMapping
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<UrlRedirectRuleDTO>> create(
            @RequestBody @Valid CreateUrlRedirectRuleDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @DeleteMapping("/{id}")
    @RateLimiter(name = "delete")
    ResponseEntity<ResponseHttp<?>> delete(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @PatchMapping("/{id}")
    @RateLimiter(name = "update")
    ResponseEntity<ResponseHttp<UrlRedirectRuleDTO>> update(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlRedirectRuleDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

}
