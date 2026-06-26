package com.read.api.application.usecase.impl.urlRedirectRule;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.DeleteUrlRedirectRuleByIdUseCase;
import com.read.api.domain.model.UrlModel;
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
public class DeleteUrlRedirectRuleByIdUseCaseImpl implements DeleteUrlRedirectRuleByIdUseCase {

    UrlRedirectRuleRepository repository;
    UrlRepository urlRepository;

    @Override
    @ObservedMetric("url.access.rule.delete.id")
    public Result<Void> execute(Long id) {
        Long urlId = repository.findUrlIdById(id).orElse(null);

        if (urlId == null) {
            return Result.failure(
                    404,
                    "Url Redirect Rule not found"
            );
        }

        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(
                    404,
                    "Url Redirect Rule not found"
            );
        }

        UrlModel url = urlRepository.findById(urlId).orElse(null);

        if (url != null) {
            url.getMetric().decrementRedirectRuleCount();

            urlRepository.save(url);
        }

        return Result.success();
    }
}
