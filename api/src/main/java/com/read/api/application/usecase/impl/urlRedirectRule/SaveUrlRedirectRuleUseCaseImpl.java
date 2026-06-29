package com.read.api.application.usecase.impl.urlRedirectRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.SaveUrlRedirectRuleUseCase;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveUrlRedirectRuleUseCaseImpl implements SaveUrlRedirectRuleUseCase {
    UrlRedirectRuleRepository repository;

    @Override
    @Retry(name = "save")
    @ObservedMetric("url.access.rule.save")
    public Result<UrlRedirectRuleModel> execute(UrlRedirectRuleModel model) {
        var inserted = repository.save(model);

        return Result.success(inserted, 200);
    }
}
