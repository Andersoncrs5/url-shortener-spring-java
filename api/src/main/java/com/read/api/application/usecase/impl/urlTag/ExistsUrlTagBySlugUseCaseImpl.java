package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.ExistsUrlTagBySlugUseCase;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExistsUrlTagBySlugUseCaseImpl implements ExistsUrlTagBySlugUseCase {
    UrlTagRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.tag.exists.slug")
    public Result<Boolean> execute(String slug) {
        return Result.success(repository.existsBySlug(slug));
    }
}
