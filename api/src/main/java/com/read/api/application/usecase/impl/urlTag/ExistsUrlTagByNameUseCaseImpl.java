package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.ExistsUrlTagByNameUseCase;
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
public class ExistsUrlTagByNameUseCaseImpl implements ExistsUrlTagByNameUseCase {
    UrlTagRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.tag.exists.name")
    public Result<Boolean> execute(String name) {
        return Result.success(repository.existsByName(name));
    }

}
