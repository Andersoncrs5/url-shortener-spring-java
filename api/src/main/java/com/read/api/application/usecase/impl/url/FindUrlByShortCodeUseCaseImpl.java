package com.read.api.application.usecase.impl.url;

import com.read.api.application.usecase.interfaces.url.FindUrlByShortCodeUseCase;
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
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindUrlByShortCodeUseCaseImpl implements FindUrlByShortCodeUseCase {
    UrlRepository repository;
    RedisCrudService redis;

    @Override
    public Result<UrlModel> execute(String code) {
        String key = "url:" + code;

        var cached = redis.find(key, UrlModel.class);

        if (cached.isPresent()) {
            return Result.success(cached.get());
        }

        Optional<UrlModel> optional = repository.findByShortCode(code);

        if (optional.isEmpty()) {
            return Result.failure(
                    "Url not found",
                    404
            );
        }

        redis.save(
                key,
                optional.get(),
                Duration.ofMinutes(10)
        );

        return Result.success(optional.get());
    }

}
