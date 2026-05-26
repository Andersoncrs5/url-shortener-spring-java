package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UrlControllerDocs {

    @Idempotent
    @PostMapping
    ResponseEntity<ResponseHttp<UrlResponseDTO>> create(
            @RequestBody @Valid CreateUrlDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseHttp<?>> delete(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @DeleteMapping("/{id}/force")
    ResponseEntity<ResponseHttp<?>> deleteForce(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @Idempotent
    @PatchMapping("/{id}")
    ResponseEntity<ResponseHttp<?>> update(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

}
