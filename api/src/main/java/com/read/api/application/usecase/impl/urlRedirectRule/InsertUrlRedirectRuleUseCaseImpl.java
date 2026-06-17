package com.read.api.application.usecase.impl.urlRedirectRule;

import com.read.api.application.usecase.interfaces.urlRedirectRule.InsertUrlRedirectRuleUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertUrlRedirectRuleUseCaseImpl implements InsertUrlRedirectRuleUseCase {
    UrlRedirectRuleRepository repository;

    @Override
    public Result<UrlRedirectRuleModel> execute(UrlRedirectRuleModel model) {
        var inserted = repository.insert(model);

        return Result.success(inserted, 201);
    }
}
