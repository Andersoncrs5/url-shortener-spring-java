package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.ports.out.repository.shared.CrudRepository;
import com.write.api.shared.validation.snowflake.IsId;

import java.util.List;

public interface IUserRoleRepository extends CrudRepository<UserRoleModel, Long> {
    List<String> findRoleByUserId(@IsId Long id);
    boolean existsByRoleIdAndUserId(@IsId Long roleId, @IsId Long userId);
}
