package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.shared.validation.snowflake.IsId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface UrlTagLinkControllerDocs {

    @Idempotent
    @PostMapping
    ResponseEntity<ResponseHttp<UrlTagLinkDTO>> create(
            @RequestBody @Valid CreateUrlTagLinkDTO dto,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @DeleteMapping("/{id}")
    ResponseEntity<ResponseHttp<?>> del(
            @PathVariable @IsId Long id,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @Idempotent
    @PatchMapping("/{id}")
    ResponseEntity<ResponseHttp<UrlTagLinkDTO>> create(
            @PathVariable @IsId Long id,
            @RequestBody @Valid UpdateUrlTagLinkDTO dto,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );
}
