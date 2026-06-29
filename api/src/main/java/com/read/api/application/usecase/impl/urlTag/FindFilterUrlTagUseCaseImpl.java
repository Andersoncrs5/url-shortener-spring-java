package com.read.api.application.usecase.impl.urlTag;

import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.FindFilterUrlTagUseCase;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
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
public class FindFilterUrlTagUseCaseImpl implements FindFilterUrlTagUseCase {
    UrlTagRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.tag.find.all.filter")
    public Page<UrlTagModel> execute(UrlTagFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }
}
