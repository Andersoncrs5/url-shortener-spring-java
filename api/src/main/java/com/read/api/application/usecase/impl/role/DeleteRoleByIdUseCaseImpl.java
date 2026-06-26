package com.read.api.application.usecase.impl.role;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.role.DeleteRoleByIdUseCase;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteRoleByIdUseCaseImpl implements DeleteRoleByIdUseCase {
    RoleRepository repository;

    @Override
    @ObservedMetric("role.delete.id")
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Role not found");
        }

        return Result.success();
    }

}
