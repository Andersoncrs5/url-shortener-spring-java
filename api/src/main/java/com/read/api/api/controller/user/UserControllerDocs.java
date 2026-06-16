package com.read.api.api.controller.user;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.user.UserDTO;
import com.read.api.api.dto.user.UserFilter;
import com.read.api.utils.annotation.idempotent.Idempotent;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UserControllerDocs {

    @Idempotent
    @GetMapping
    ResponseEntity<Page<?>> getAll(
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey,
            @ModelAttribute UserFilter filter,
            @ModelAttribute UserPageRequestDTO page
    );

    @GetMapping("/email-exists")
    ResponseEntity<ResponseHTTP<Boolean>> emailExists(
            @RequestParam String email
    );

    @GetMapping("/name-exists")
    ResponseEntity<ResponseHTTP<Boolean>> nameExists(
            @RequestParam String name
    );

    @Idempotent
    @GetMapping("/{id}")
    ResponseEntity<ResponseHTTP<UserDTO>> findById(
            @PathVariable Long id,
            @Parameter(
                    description = "Unique key used to guarantee idempotency and avoid duplicate API Key creation",
                    required = true,
                    example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234"
            )
            @RequestHeader("X-Idempotency-Key")
            String idempotencyKey
    );

}
