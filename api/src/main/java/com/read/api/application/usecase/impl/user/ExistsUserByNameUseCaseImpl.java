package com.read.api.application.usecase.impl.user;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.user.ExistsUserByNameUseCase;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExistsUserByNameUseCaseImpl implements ExistsUserByNameUseCase {
    UserRepository repository;

    @Override
    @ObservedMetric("user.exists.name")
    public boolean execute(String name) {
        return repository.existsByNameIgnoreCase(name);
    }
}
