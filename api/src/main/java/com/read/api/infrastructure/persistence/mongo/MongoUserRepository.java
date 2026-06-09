package com.read.api.infrastructure.persistence.mongo;

import com.read.api.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUserRepository extends MongoRepository<UserEntity, Long> {
}
