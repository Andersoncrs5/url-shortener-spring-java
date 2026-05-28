package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UrlRedirectRuleControllerDocs;
import com.write.api.adapters.in.web.mapper.UrlRedirectRuleMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.ports.in.urlRedirectRule.CreateUrlRedirectRuleUseCase;
import com.write.api.ports.in.urlRedirectRule.DeleteUrlRedirectRuleUseCase;
import com.write.api.ports.in.urlRedirectRule.UpdateUrlRedirectRuleUseCase;
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
@RequestMapping("v1/url-redirect-rule")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlRedirectRuleController implements UrlRedirectRuleControllerDocs {

    CreateUrlRedirectRuleUseCase createUrlRedirectRule;
    UpdateUrlRedirectRuleUseCase updateUrlRedirectRule;
    DeleteUrlRedirectRuleUseCase deleteUrlRedirectRule;
    UrlRedirectRuleMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<UrlRedirectRuleDTO>> update(
            Long id,
            UpdateUrlRedirectRuleDTO dto,
            String idempotencyKey
    ) {
        Result<UrlRedirectRuleModel> result = updateUrlRedirectRule.execute(id, dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toDTO(result.getValue()),
                                "Rule updated",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> delete(
            Long id,
            String idempotencyKey
    ) {
        Result<Void> result = deleteUrlRedirectRule.execute(id);
        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Rule deleted",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<UrlRedirectRuleDTO>> create(
            CreateUrlRedirectRuleDTO dto,
            String idempotencyKey
    ) {
        Result<UrlRedirectRuleModel> result = createUrlRedirectRule.execute(dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toDTO(result.getValue()),
                                "Rule created",
                                idempotencyKey
                        )
                );
    }
}
