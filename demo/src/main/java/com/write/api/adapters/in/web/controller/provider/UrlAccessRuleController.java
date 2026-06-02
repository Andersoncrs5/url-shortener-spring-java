package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UrlAccessRuleControllerDocs;
import com.write.api.adapters.in.web.mapper.UrlAccessRuleMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.ports.in.urlAccessRule.CreateUrlAccessRuleUseCase;
import com.write.api.ports.in.urlAccessRule.DeleteUrlAccessRuleUseCase;
import com.write.api.ports.in.urlAccessRule.UpdateUrlAccessRuleUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/url-access-rule")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlAccessRuleController implements UrlAccessRuleControllerDocs {

    CreateUrlAccessRuleUseCase createUrlAccessRule;
    DeleteUrlAccessRuleUseCase deleteUrlAccessRuleUseCase;
    UpdateUrlAccessRuleUseCase updateUrlAccessRule;
    UrlAccessRuleMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<?>> create(
            CreateUrlAccessRuleDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        var result = createUrlAccessRule.execute(dto, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                .status(result.getStatusCode())
                .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
            .body(
                ResponseHttp.success(
                    mapper.toDTO(result.getValue()),
                    "Url Access Rule created",
                    idempotencyKey
                )
            );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> update(
            Long id,
            UpdateUrlAccessRuleDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        var result = updateUrlAccessRule.execute(dto, id);

        if (result.isFailure()) {
            return ResponseEntity
                .status(result.getStatusCode())
                .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
            .body(
                ResponseHttp.success(
                    mapper.toDTO(result.getValue()),
                    "Url Access Rule updated",
                    idempotencyKey
                )
            );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> delete(
            Long id,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<Void> result = deleteUrlAccessRuleUseCase.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Url Access Rule deleted",
                                idempotencyKey
                        )
                );
    }

}
