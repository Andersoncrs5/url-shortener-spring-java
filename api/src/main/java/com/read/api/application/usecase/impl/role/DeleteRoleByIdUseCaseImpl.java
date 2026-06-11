package com.read.api.application.usecase.impl.role;

import com.read.api.application.usecase.interfaces.role.DeleteRoleByIdUseCase;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteRoleByIdUseCaseImpl implements DeleteRoleByIdUseCase {
    RoleRepository repository;

    @Override
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Role not found");
        }

        return Result.success();
    }

}
