package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UrlTagControllerDocs;
import com.write.api.adapters.in.web.mapper.UrlTagMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.application.shared.Result;
import com.write.api.config.security.classes.UserPrincipal;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.ports.in.urlTag.CreateUrlTagUseCase;
import com.write.api.ports.in.urlTag.DeleteByIdUseCase;
import com.write.api.ports.in.urlTag.UpdateUrlTagUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated @RestController
@RequiredArgsConstructor @RequestMapping("v1/url-tag")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlTagController implements UrlTagControllerDocs {

    CreateUrlTagUseCase createUrlTag;
    DeleteByIdUseCase deleteById;
    UpdateUrlTagUseCase updateUrlTagUse;
    UrlTagMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<UrlTagResponseDTO>> create(
            CreateUrlTagDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<UrlTagModel> result = createUrlTag.execute(dto, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toResponse(result.getValue()),
                                "Tag created",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> del(
            Long id,
            String idempotencyKey
    ) {
        Result<Void> result = this.deleteById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Tag deleted",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<UrlTagResponseDTO>> create(
            Long id,
            UpdateUrlTagDTO dto,
            String idempotencyKey
    ) {
        Result<UrlTagModel> result = this.updateUrlTagUse.execute(id, dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toResponse(result.getValue()),
                                "Tag updated",
                                idempotencyKey
                        )
                );
    }

}
