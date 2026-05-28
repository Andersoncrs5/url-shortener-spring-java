package com.write.api.ports.in.role;

import com.write.api.application.dto.role.CreateRoleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.RoleModel;

public interface CreateRoleUseCase {
    Result<RoleModel> execute(CreateRoleDTO dto);
}
