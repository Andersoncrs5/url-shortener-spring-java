package com.read.api.application.usecase.impl.urlTag;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.FindUrlTagByIdUseCase;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindUrlTagByIdUseCaseImpl implements FindUrlTagByIdUseCase {
    UrlTagRepository repository;

    @Override
    @Cacheable(value = "tag", key = "#id", unless = "!#result.isSuccess()")
    public Result<UrlTagModel> execute(Long id) {
        return repository.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("Url Tag not found", 404) );
    }
}
