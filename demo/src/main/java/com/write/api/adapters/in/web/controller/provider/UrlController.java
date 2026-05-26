package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UrlControllerDocs;
import com.write.api.adapters.in.web.mapper.UrlMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.application.shared.Result;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.url.CreateUrlUseCase;
import com.write.api.ports.in.url.DeleteUrlByIdForceUseCase;
import com.write.api.ports.in.url.DeleteUrlByIdSoftUseCase;
import com.write.api.ports.in.url.UpdateUrlUseCase;
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
@RequestMapping("v1/url")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlController implements UrlControllerDocs {

    UrlMapper mapper;
    CreateUrlUseCase createUrl;
    DeleteUrlByIdSoftUseCase deleteUrlByIdSoft;
    DeleteUrlByIdForceUseCase deleteUrlByIdForce;
    UpdateUrlUseCase updateUrl;

    @Override
    public ResponseEntity<ResponseHttp<UrlResponseDTO>> create(
            CreateUrlDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<UrlModel> result = createUrl.execute(dto, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toResponse(result.getValue()),
                                "Url created",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> delete(
            Long id,
            String idempotencyKey
    ) {
        Result<Void> result = deleteUrlByIdSoft.execute(id);
        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toResponse(null),
                                "Url deleted",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> deleteForce(
            Long id,
            String idempotencyKey
    ) {
        Result<Void> result = deleteUrlByIdForce.execute(id);
        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Url deleted forced",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> update(
            Long id,
            UpdateUrlDTO dto,
            String idempotencyKey
    ) {
        Result<UrlModel> result = updateUrl.execute(id,dto);
        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toResponse(result.getValue()),
                                "Url updated",
                                idempotencyKey
                        )
                );
    }

}
