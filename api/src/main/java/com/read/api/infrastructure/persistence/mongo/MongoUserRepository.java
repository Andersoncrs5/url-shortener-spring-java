package com.read.api.infrastructure.persistence.mongo;

import com.read.api.domain.model.UserModel;
import com.read.api.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUserRepository extends MongoRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByNameIgnoreCase(String email);
}
