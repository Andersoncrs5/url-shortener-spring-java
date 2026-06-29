package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.user.FindByIdUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindByIdUserUseCaseImpl implements FindByIdUserUseCase {
    UserRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("user.find.id")
    @Cacheable(value = "users", key = "#id")
    public Result<UserModel> execute(Long id) {
        var opt = repository.findById(id);

        return opt.map(Result::success)
                .orElseGet(() -> Result.failure("User not found", 404));
    }
}
