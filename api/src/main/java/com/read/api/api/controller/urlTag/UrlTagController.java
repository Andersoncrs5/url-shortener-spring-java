package com.read.api.api.controller.urlTag;

import com.read.api.api.controller.base.RestApiController;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.tag.UrlTagDTO;
import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.application.usecase.interfaces.urlTag.ExistsUrlTagByNameUseCase;
import com.read.api.application.usecase.interfaces.urlTag.ExistsUrlTagBySlugUseCase;
import com.read.api.application.usecase.interfaces.urlTag.FindFilterUrlTagUseCase;
import com.read.api.application.usecase.interfaces.urlTag.FindUrlTagByIdUseCase;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.utils.annotation.ratelimit.RateLimited;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@RestApiController("v1/url-tag")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlTagController implements UrlTagControllerDocs {
    UrlTagMapperController mapper;

    FindFilterUrlTagUseCase findAll;
    FindUrlTagByIdUseCase findById;

    ExistsUrlTagByNameUseCase existsByName;
    ExistsUrlTagBySlugUseCase existsBySlug;

    @Override
    @RateLimited("read-low")
    public ResponseEntity<Page<UrlTagDTO>> findAllFilter(
            UrlTagFilter filter,
            UrlTagPageRequestDTO page
    ) {
        Page<UrlTagModel> result = findAll.execute(filter, page.toPageable());

        var items = result.map(mapper::toDTO);

        return ResponseEntity.ok(items);
    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<UrlTagDTO>> findById(
            Long id
    ) {
        Result<UrlTagModel> result = findById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage()));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHTTP.success(
                                mapper.toDTO(result.getValue()),
                                "Url Tag not found"
                        )
                );

    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<Boolean>> nameExists(
            String name
    ) {

        Result<Boolean> result =
                existsByName.execute(name);

        return ResponseEntity.ok(
                ResponseHTTP.success(
                        result.getValue(),
                        "Name checked"
                )
        );
    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<Boolean>> slugExists(
            String slug
    ) {

        Result<Boolean> result = existsBySlug.execute(slug);

        return ResponseEntity.ok(
                ResponseHTTP.success(
                        result.getValue(),
                        "Slug checked"
                )
        );
    }

}
