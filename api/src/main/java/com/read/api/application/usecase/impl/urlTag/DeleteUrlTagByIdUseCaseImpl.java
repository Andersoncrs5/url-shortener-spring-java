package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.DeleteUrlTagByIdUseCase;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlTagByIdUseCaseImpl implements DeleteUrlTagByIdUseCase {
    UrlTagRepository repository;

    @Override
    @CacheEvict(value = "tag", key = "#id")
    @ObservedMetric("url.tag.delete.id")
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted <= 0) {
            return Result.failure(404, "Url tag not found");
        }

        return Result.success();
    }

}
