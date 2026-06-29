package com.read.api.application.usecase.impl.role;

import io.github.resilience4j.retry.annotation.Retry;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.role.SaveRoleUseCase;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaveRoleUseCaseImpl implements SaveRoleUseCase {
    RoleRepository repository;

    @Override
    @Retry(name = "save")
    @ObservedMetric("role.save")
    public Result<RoleModel> execute(RoleModel role) {
        RoleModel saved = repository.save(role);

        return Result.success(saved);
    }
}
