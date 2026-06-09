package com.read.api.infrastructure.persistence.mongo;

import com.read.api.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoRoleRepository extends MongoRepository<RoleEntity, Long> {
}
