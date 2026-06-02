package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UrlAccessRuleControllerDocs {
    @Idempotent
    @PostMapping
    ResponseEntity<ResponseHttp<?>> create(
            @RequestBody @Valid CreateUrlAccessRuleDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseHttp<?>> delete(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @PatchMapping("/{id}")
    ResponseEntity<ResponseHttp<?>> update(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlAccessRuleDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

}
