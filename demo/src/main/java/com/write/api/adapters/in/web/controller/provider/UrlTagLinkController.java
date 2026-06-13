package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UrlTagLinkControllerDocs;
import com.write.api.adapters.in.web.mapper.UrlTagLinkMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.ports.in.urlTagLink.CreateUrlTagLinkUseCase;
import com.write.api.ports.in.urlTagLink.DeleteUrlTagLinkByIdUseCase;
import com.write.api.ports.in.urlTagLink.UpdateUrlTagLinkUseCase;
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
@RequestMapping("v1/url-tag-link")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlTagLinkController implements UrlTagLinkControllerDocs {

    CreateUrlTagLinkUseCase createUrlTagLink;
    DeleteUrlTagLinkByIdUseCase deleteUrlTagLinkById;
    UpdateUrlTagLinkUseCase updateUrlTagLink;
    UrlTagLinkMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<UrlTagLinkDTO>> create(
            CreateUrlTagLinkDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<UrlTagLinkModel> result = createUrlTagLink.execute(dto, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toDTO(result.getValue()),
                                "Tag linked to url",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> del(
            Long id,
            String idempotencyKey
    ) {
        Result<Void> result = this.deleteUrlTagLinkById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Tag unlinked of url",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<UrlTagLinkDTO>> update(
            Long id,
            UpdateUrlTagLinkDTO dto,
            String idempotencyKey
    ) {
        Result<UrlTagLinkModel> result = this.updateUrlTagLink.execute(dto, id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toDTO(result.getValue()),
                                "Tag linked in url updated",
                                idempotencyKey
                        )
                );
    }

}
