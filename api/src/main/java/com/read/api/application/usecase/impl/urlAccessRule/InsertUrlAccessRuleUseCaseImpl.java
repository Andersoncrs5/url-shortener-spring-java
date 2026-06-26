package com.read.api.application.usecase.impl.urlAccessRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.InsertUrlAccessRuleUseCase;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertUrlAccessRuleUseCaseImpl implements InsertUrlAccessRuleUseCase {
    UrlAccessRuleRepository repository;
    UrlRepository urlRepository;

    @Override
    @ObservedMetric("url.access.rule.insert")
    public Result<UrlAccessRuleModel> execute(UrlAccessRuleModel model) {
        UrlModel url = urlRepository.findById(model.getUrlId()).orElse(null);

        if (url == null) {
            return Result.failure("Url not found", 404);
        }

        UrlAccessRuleModel inserted = repository.insert(model);

        url.getMetric().incrementAccessRuleCount();
        urlRepository.save(url);

        return Result.success(inserted, 201);
    }

}
