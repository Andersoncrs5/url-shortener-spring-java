package com.write.api.ports.in.userRole;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface CreateUserRoleUseCase {
    Result<UserRoleModel> execute(CreateUserRoleDTO dto, @IsId Long assignedByUserId);
}
