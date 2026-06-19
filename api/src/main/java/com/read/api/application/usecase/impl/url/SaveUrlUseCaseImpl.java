package com.read.api.application.usecase.impl.url;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.SaveUrlUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Duration;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveUrlUseCaseImpl implements SaveUrlUseCase {
    UrlRepository repository;
    RedisCrudService redis;

    @Override
    public Result<UrlModel> execute(UrlModel url) {
        UrlModel saved = repository.save(url);
        String key = "url:" + saved.getShortCode();

        redis.save(
                key,
                saved,
                Duration.ofMinutes(10)
        );

        return Result.success(saved);
    }
}
