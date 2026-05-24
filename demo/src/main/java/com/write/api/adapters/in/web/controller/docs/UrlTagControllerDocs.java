package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.config.api.idempotent.Idempotent;
import com.write.api.config.security.classes.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UrlTagControllerDocs {

    @Idempotent
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseHttp<?>> del(
            @PathVariable Long id,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @Idempotent
    @PostMapping
    ResponseEntity<ResponseHttp<UrlTagResponseDTO>> create(
            @RequestBody @Valid CreateUrlTagDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @PatchMapping("/{id}")
    ResponseEntity<ResponseHttp<UrlTagResponseDTO>> create(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUrlTagDTO dto,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );
}
