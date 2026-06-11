package com.read.api.domain.repository;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.base.BaseRepository;

public interface RoleRepository extends BaseRepository<RoleModel, Long, RoleFilter> {
}
