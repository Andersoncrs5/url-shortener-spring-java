package com.read.api.infrastructure.persistence.mongo;

import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.infrastructure.persistence.entity.UrlAccessRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MongoUrlAccessRuleRepository extends MongoRepository<UrlAccessRuleEntity, Long> {
    List<UrlAccessRuleEntity> findAllByUrlIdAndActiveTrueAndExpiresAtAfter(Long urlId, LocalDateTime now);
    boolean existsByUrlIdAndTypeAndRuleValue(
            Long urlId,
            UrlAccessRuleTypeEnum type,
            String ruleValue
    );
}
