package com.read.api.api.controller.urlAccessRule;

import com.read.api.api.controller.base.RestApiController;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.application.usecase.interfaces.urlAccessRule.ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllFilterUrlAccessRuleUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindUrlAccessRuleByIdUseCase;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestApiController("v1/url-access-rule")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlAccessRuleController implements UrlAccessRuleControllerDocs {
    UrlAccessRuleMapperController mapper;
    FindAllFilterUrlAccessRuleUseCase findAll;
    FindUrlAccessRuleByIdUseCase findById;
    ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase exists;

    @Override
    public ResponseEntity<ResponseHTTP<Boolean>> exists(
            Long urlId,
            UrlAccessRuleTypeEnum type,
            String ruleValue
    ) {

        boolean exists =
                this.exists.execute(
                        urlId,
                        type,
                        ruleValue
                );

        return ResponseEntity.ok(
                ResponseHTTP.success(
                        exists,
                        exists
                                ? "Rule already exists"
                                : "Rule not found"
                )
        );
    }

    @Override
    public ResponseEntity<Page<UrlAccessRuleDTO>> findAllFilter(
            @ModelAttribute UrlAccessRuleFilter filter,
            UrlAccessRulePageRequestDTO page
    ) {
        Page<UrlAccessRuleModel> result = findAll.execute(filter, page.toPageable());

        var items = result.map(mapper::toDTO);

        return ResponseEntity.ok(items);
    }

    @Override
    public ResponseEntity<ResponseHTTP<UrlAccessRuleDTO>> findById(
        Long id
    ) {
        Result<UrlAccessRuleModel> result = findById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage()));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHTTP.success(
                                mapper.toDTO(result.getValue()),
                                "Url Access Rule found"
                        )
                );

    }

}
