package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.InsertUrlTagUseCase;
import com.read.api.domain.model.UrlTagModel;
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
public class InsertUrlTagUseCaseImpl implements InsertUrlTagUseCase {
    UrlTagRepository repository;

    @Override
    @Retry(name = "insert")
    @ObservedMetric("url.tag.insert")
    public Result<UrlTagModel> execute(UrlTagModel model) {
        UrlTagModel save = repository.insert(model);

        return Result.success(save, 200);
    }
}
