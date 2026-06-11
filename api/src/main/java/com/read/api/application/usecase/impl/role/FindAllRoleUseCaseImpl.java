package com.read.api.application.usecase.impl.role;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.application.usecase.interfaces.role.FindAllRoleUseCaseUseCase;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllRoleUseCaseImpl implements FindAllRoleUseCaseUseCase {
    RoleRepository repository;

    @Override
    public Page<RoleModel> execute(RoleFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }
}
