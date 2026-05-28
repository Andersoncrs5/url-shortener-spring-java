package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.out.repository.shared.CrudRepository;
import jakarta.validation.constraints.NotBlank;

public interface IRoleRepository extends CrudRepository<RoleModel, Long> {
    boolean existsByNameIgnoreCase(@NotBlank String name);
}
