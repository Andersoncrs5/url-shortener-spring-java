package com.write.api.adapters.in.web.controller.docs;

import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.config.api.idempotent.Idempotent;
import com.write.api.config.security.classes.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UserControllerDocs {

    @Idempotent
    @DeleteMapping
    ResponseEntity<ResponseHttp<?>> delete(
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @Idempotent
    @PatchMapping
    ResponseEntity<ResponseHttp<?>> update(
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateUserDTO dto
    );
}
