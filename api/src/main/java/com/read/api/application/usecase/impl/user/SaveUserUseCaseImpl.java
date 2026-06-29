package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.user.SaveUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveUserUseCaseImpl implements SaveUserUseCase {
    UserRepository repository;

    @Override
    @Retry(name = "save")
    @ObservedMetric("user.save")
    public Result<UserModel> execute(UserModel user) {
        UserModel saved = repository.save(user);

        return Result.success(saved, 200);
    }

}
