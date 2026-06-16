package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.interfaces.urlAccessRule.InsertUrlAccessRuleUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
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
public class InsertUrlAccessRuleUseCaseImpl implements InsertUrlAccessRuleUseCase {
    UrlAccessRuleRepository repository;

    @Override
    public Result<UrlAccessRuleModel> execute(UrlAccessRuleModel model) {
        UrlAccessRuleModel inserted = repository.insert(model);

        return Result.success(inserted, 201);
    }

}
