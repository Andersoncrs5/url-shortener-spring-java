package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllUrlAccessRuleByUrlIdUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllUrlAccessRuleByUrlIdUseCaseImpl implements FindAllUrlAccessRuleByUrlIdUseCase {
    UrlAccessRuleRepository repository;

    @Override
    @ObservedMetric("url.access.rule.find.all.urlid")
    public List<UrlAccessRuleModel> execute(Long urlId) {
        return repository.findAllByUrlId(urlId);
    }
}
