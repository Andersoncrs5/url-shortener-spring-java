package com.read.api.application.usecase.impl.urlRedirectRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.InsertUrlRedirectRuleUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertUrlRedirectRuleUseCaseImpl implements InsertUrlRedirectRuleUseCase {

    UrlRedirectRuleRepository repository;
    UrlRepository urlRepository;

    @Override
    @ObservedMetric("url.access.rule.insert")
    public Result<UrlRedirectRuleModel> execute(UrlRedirectRuleModel model) {

        UrlModel url = urlRepository.findById(model.getUrlId()).orElse(null);

        if (url == null) {
            return Result.failure(
                    "Url not found",
                    404
            );
        }

        UrlRedirectRuleModel inserted = repository.insert(model);

        url.getMetric().incrementRedirectRuleCount();

        urlRepository.save(url);

        return Result.success(inserted, 201);
    }
}