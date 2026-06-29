package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.SaveUrlTagUseCase;
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
public class SaveUrlTagUseCaseImpl implements SaveUrlTagUseCase {
    UrlTagRepository repository;

    @Override
    @Retry(name = "save")
    @ObservedMetric("url.tag.save")
    public Result<UrlTagModel> execute(UrlTagModel model) {
        UrlTagModel save = repository.save(model);

        return Result.success(save, 200);
    }
}
