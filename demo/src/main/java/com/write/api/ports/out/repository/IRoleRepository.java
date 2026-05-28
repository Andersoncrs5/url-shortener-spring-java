package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.out.repository.shared.CrudRepository;

public interface IRoleRepository extends CrudRepository<RoleModel, Long> {
}
