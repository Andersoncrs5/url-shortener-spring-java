package com.read.api.application.usecase.impl.user;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.user.FindAllUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
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
public class FindAllUserUseCaseImpl implements FindAllUserUseCase {
    UserRepository repository;

    @Override
    @Retry(name = "read")
    @ObservedMetric("user.find.all.filter")
    public Page<UserModel> execute(UserFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }
}
