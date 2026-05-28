package com.write.api.ports.in.role;

import com.write.api.application.dto.role.UpdateRoleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface UpdateRoleUseCase {
    Result<RoleModel> execute(@IsId Long id, UpdateRoleDTO dto);
}
