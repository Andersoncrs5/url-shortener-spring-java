package com.read.api.infrastructure.persistence.mongo;

import com.read.api.infrastructure.persistence.entity.OutboxEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoOutboxEventRepository extends MongoRepository<OutboxEventEntity, Long> {
}
