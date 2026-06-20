package com.read.api.application.usecase.impl.url;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.DeleteUrlByIdUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlByIdUseCaseImpl implements DeleteUrlByIdUseCase {
    UrlRepository repository;
    RedisCrudService redis;

    @Override
    @CacheEvict(value = "url", key = "#id")
    public Result<Void> execute(Long id) {
        UrlModel url = repository.findById(id).orElse(null);

        if (url == null) {
            return Result.failure(404, "Url not found");
        }

        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url not found");
        }

        redis.delete("url:" + url.getShortCode());

        return Result.success();
    }
}
