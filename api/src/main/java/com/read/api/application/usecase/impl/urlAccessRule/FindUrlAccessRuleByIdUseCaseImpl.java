package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindUrlAccessRuleByIdUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindUrlAccessRuleByIdUseCaseImpl implements FindUrlAccessRuleByIdUseCase {
    UrlAccessRuleRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.access.rule.find.id")
    public Result<UrlAccessRuleModel> execute(Long id) {
        return repository.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("Url Access Rule not found", 404) );
    }
}
