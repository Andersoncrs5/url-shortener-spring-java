package com.write.api.ports.in.userRole;

import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteUserRoleUseCase {
    Result<Void> deleteById(@IsId Long id);
}
