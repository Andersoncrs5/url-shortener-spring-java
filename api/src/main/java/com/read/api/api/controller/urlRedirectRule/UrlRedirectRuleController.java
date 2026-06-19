package com.read.api.api.controller.urlRedirectRule;

import com.read.api.api.controller.base.RestApiController;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.application.usecase.interfaces.urlRedirectRule.FindAllFilterUrlRedirectRuleUseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.FindUrlRedirectRuleByIdUseCase;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@RestApiController("v1/url-redirect-rule")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlRedirectRuleController implements UrlRedirectRuleControllerDocs {
    FindAllFilterUrlRedirectRuleUseCase findAll;
    FindUrlRedirectRuleByIdUseCase findById;
    UrlRedirectRuleMapperController mapper;

    @Override
    public ResponseEntity<Page<UrlRedirectRuleDTO>> findAllFilter(
            UrlRedirectRuleFilter filter,
            UrlRedirectRulePageRequestDTO page
    ) {
        var result = findAll.execute(filter, page.toPageable());

        var items = result.map(mapper::toDTO);

        return ResponseEntity.ok(items);
    }


    @Override
    public ResponseEntity<ResponseHTTP<UrlRedirectRuleDTO>> findById(Long id) {
        Result<UrlRedirectRuleModel> result = findById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage()));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHTTP.success(
                                mapper.toDTO(result.getValue()),
                                "Url Redirect Rule found"
                        )
                );

    }


}
