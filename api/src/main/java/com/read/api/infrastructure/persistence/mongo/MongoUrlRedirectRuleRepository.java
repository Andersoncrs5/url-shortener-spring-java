package com.read.api.infrastructure.persistence.mongo;

import com.read.api.infrastructure.persistence.entity.UrlRedirectRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoUrlRedirectRuleRepository extends MongoRepository<UrlRedirectRuleEntity, Long> {
}
