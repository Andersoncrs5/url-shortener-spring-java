package com.read.api.application.usecase.impl.role;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.role.InsertRoleUseCase;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InsertRoleUseCaseImpl implements InsertRoleUseCase {
    RoleRepository repository;

    @Override
    @Retry(name = "insert")
    @ObservedMetric("role.insert")
    public Result<RoleModel> execute(RoleModel role) {
        RoleModel saved = repository.insert(role);

        return Result.success(saved);
    }
}
