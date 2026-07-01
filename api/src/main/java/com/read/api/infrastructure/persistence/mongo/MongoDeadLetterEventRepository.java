package com.read.api.infrastructure.persistence.mongo;

import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.infrastructure.persistence.entity.DeadLetterEventEntity;
import com.read.api.utils.validation.isId.IsId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoDeadLetterEventRepository extends MongoRepository<DeadLetterEventEntity, Long> {
    boolean existsByEventId(@IsId Long id);
    Optional<DeadLetterEventEntity> findByEventId(Long id);
}
