package com.read.api.infrastructure.persistence.mongo;

import com.read.api.infrastructure.persistence.entity.UrlTagEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUrlTagRepository extends MongoRepository<UrlTagEntity, Long> {
    boolean existsByName(@NotBlank String name);
    boolean existsBySlug(@NotBlank String slug);
}
