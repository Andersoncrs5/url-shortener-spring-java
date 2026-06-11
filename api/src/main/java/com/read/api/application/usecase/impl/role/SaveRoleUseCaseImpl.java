package com.read.api.application.usecase.impl.role;

import com.read.api.application.usecase.interfaces.role.SaveRoleUseCase;
import com.read.api.domain.model.RoleModel;
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
public class SaveRoleUseCaseImpl implements SaveRoleUseCase {
    RoleRepository repository;

    @Override
    public Result<RoleModel> execute(RoleModel role) {
        RoleModel saved = repository.save(role);

        return Result.success(saved);
    }
}
