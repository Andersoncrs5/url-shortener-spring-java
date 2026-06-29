package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCaseImpl
        implements ExistsByUrlIdAndTypeAndRuleValueUrlAccessRuleUseCase {
    UrlAccessRuleRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.access.rule.exists.url.id.type.value")
    public boolean execute(Long urlId, UrlAccessRuleTypeEnum type, String ruleValue) {
        return repository.existsUnique(urlId, type, ruleValue);
    }

}
