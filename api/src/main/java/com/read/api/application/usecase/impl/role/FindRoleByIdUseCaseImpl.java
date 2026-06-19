package com.read.api.application.usecase.impl.role;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.role.FindRoleByIdUseCase;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.Cacheable;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindRoleByIdUseCaseImpl implements FindRoleByIdUseCase {
    RoleRepository repository;

    @Override
    @Cacheable(value = "roles", key = "#id")
    public Result<RoleModel> execute(Long id) {
        var opt = repository.findById(id);

        return opt.map(Result::success)
                .orElseGet(() -> Result.failure("Role not found", 404));
    }

}
