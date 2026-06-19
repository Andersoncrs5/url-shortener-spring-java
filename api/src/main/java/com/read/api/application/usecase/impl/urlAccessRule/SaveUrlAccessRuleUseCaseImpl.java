package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.SaveUrlAccessRuleUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveUrlAccessRuleUseCaseImpl implements SaveUrlAccessRuleUseCase {
    UrlAccessRuleRepository repository;

    @Override
    public Result<UrlAccessRuleModel> execute(UrlAccessRuleModel model) {
        UrlAccessRuleModel save = repository.save(model);

        return Result.success(save, 200);
    }

}
