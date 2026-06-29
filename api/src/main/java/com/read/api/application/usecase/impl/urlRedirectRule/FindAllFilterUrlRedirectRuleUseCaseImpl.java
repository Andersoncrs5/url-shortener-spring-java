package com.read.api.application.usecase.impl.urlRedirectRule;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.FindAllFilterUrlRedirectRuleUseCase;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllFilterUrlRedirectRuleUseCaseImpl implements FindAllFilterUrlRedirectRuleUseCase {
    UrlRedirectRuleRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.access.rule.find.all.filter")
    public Page<UrlRedirectRuleModel> execute(UrlRedirectRuleFilter filer, Pageable pageable) {
        return repository.findAll(filer, pageable);
    }
}
