package com.read.api.application.usecase.interfaces.role;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.domain.model.RoleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllRoleUseCaseUseCase {
    Page<RoleModel> execute(RoleFilter filter, Pageable pageable);
}
