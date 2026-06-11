package com.read.api.infrastructure.persistence.mongo;

import com.read.api.infrastructure.persistence.entity.UrlEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoUrlRepository extends MongoRepository<UrlEntity, Long> {
    Optional<UrlEntity> findByShortCode(String code);
}
