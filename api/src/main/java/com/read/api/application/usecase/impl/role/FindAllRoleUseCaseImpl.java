package com.read.api.application.usecase.impl.role;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.role.FindAllRoleUseCaseUseCase;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindAllRoleUseCaseImpl implements FindAllRoleUseCaseUseCase {
    RoleRepository repository;

    @Override
    @ObservedMetric("role.find.all.filter")
    public Page<RoleModel> execute(RoleFilter filter, Pageable pageable) {
        return repository.findAll(filter, pageable);
    }
}
