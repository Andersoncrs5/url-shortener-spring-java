package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UserRoleControllerDocs {

    @Idempotent
    @PostMapping("/add-role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "create")
    ResponseEntity<ResponseHttp<?>> create(
            @Valid @RequestBody CreateUserRoleDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );


    @Idempotent
    @DeleteMapping("remove-role/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @RateLimiter(name = "delete")
    ResponseEntity<ResponseHttp<?>> delete(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );
}
