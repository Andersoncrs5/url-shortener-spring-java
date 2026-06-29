package com.read.api.application.usecase.impl.url;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.FindAllUrlUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
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
public class FindAllUrlUseCaseImpl implements FindAllUrlUseCase {
    UrlRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.find.all.filter")
    public Page<UrlModel> execute(UrlFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }

}
