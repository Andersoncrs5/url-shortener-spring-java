package com.read.api.application.usecase.impl.url;

import com.read.api.application.usecase.interfaces.url.InsertUrlUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertUrlUseCaseImpl implements InsertUrlUseCase {
    UrlRepository repository;
    RedisCrudService redis;

    public Result<UrlModel> execute(UrlModel url) {
        UrlModel inserted = repository.insert(url);
        String key = "url:" + inserted.getShortCode();

        redis.save(
                key,
                inserted,
                Duration.ofMinutes(10)
        );

        return Result.success(inserted);
    }

}
