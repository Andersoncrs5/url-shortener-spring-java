package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.ports.out.repository.shared.CrudRepository;

public interface IUserRoleRepository extends CrudRepository<UserRoleModel, Long> {
}
