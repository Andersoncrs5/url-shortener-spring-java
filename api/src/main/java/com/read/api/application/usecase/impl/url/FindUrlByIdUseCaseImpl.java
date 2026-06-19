package com.read.api.application.usecase.impl.url;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.FindUrlByIdUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindUrlByIdUseCaseImpl implements FindUrlByIdUseCase {
    UrlRepository repository;

    @Override
    public Result<UrlModel> execute(Long id) {
        return repository.findById(id)
                .map(Result::success)
                .orElseGet(
                        () -> Result.failure("Url not found", 404)
                );
    }
}
