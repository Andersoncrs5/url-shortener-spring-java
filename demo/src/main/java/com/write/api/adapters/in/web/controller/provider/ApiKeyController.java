package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.ApiKeyControllerDocs;
import com.write.api.adapters.in.web.mapper.ApiKeyMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.apiKey.ApiKeyDTO;
import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.apiKey.UpdateApiKeyDTO;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.ports.in.apiKey.CreateApiKeyUseCase;
import com.write.api.ports.in.apiKey.DeleteApiKeyUseCase;
import com.write.api.ports.in.apiKey.UpdateApiKeyUseCase;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/api-key")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyController implements ApiKeyControllerDocs {

    CreateApiKeyUseCase createApiKey;
    DeleteApiKeyUseCase deleteApiKey;
    UpdateApiKeyUseCase updateApiKey;
    ApiKeyMapper mapper;

    @GetMapping("/test-logs")
    @RateLimiter(name = "test")
    public void create() {
        log.info("Test INFO");
        log.warn("Test WARN");
        log.debug("Test DEBUG");
        log.trace("Test Trace");
    }

    public ResponseEntity<String> test(String key) {
        return ResponseEntity.ok(key);
    }

    public ResponseEntity<ResponseHttp<String>> create(
            CreateApiKeyDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<String> result = createApiKey.execute(dto, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                result.getValue(),
                                "Api key created",
                                idempotencyKey
                        )
                );
    }

    public ResponseEntity<ResponseHttp<ApiKeyDTO>> update(
            Long id,
            UpdateApiKeyDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<ApiKeyModel> result = updateApiKey.execute(dto, id, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toDTO(result.getValue()),
                                "Api key updated",
                                idempotencyKey
                        )
                );
    }

    public ResponseEntity<ResponseHttp<Void>> delete(
            Long id,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<Void> result = deleteApiKey.execute(id, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Api key deleted",
                                idempotencyKey
                        )
                );
    }

}
