package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UrlAccessRuleEntity;
import com.read.api.infrastructure.persistence.mapper.UrlAccessRuleMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUrlAccessRuleRepository;
import com.read.api.infrastructure.persistence.shared.MongoRetryTranslation;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlAccessRuleRepositoryImpl
        extends BaseRepositoryImpl<UrlAccessRuleModel, UrlAccessRuleEntity, Long>
        implements UrlAccessRuleRepository {

    UrlAccessRuleMapperRepository mapper;
    MongoUrlAccessRuleRepository repository;

    public UrlAccessRuleRepositoryImpl(
            MongoTemplate template,
            UrlAccessRuleMapperRepository mapper,
            MongoUrlAccessRuleRepository repository,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    @Retry(name = "database")
    public Optional<Long> findUrlIdById(Long id) {
        return retryTranslator.execute(() -> {
            Query query = Query.query(Criteria.where("id").is(id));
            query.fields().include("urlId");

            UrlAccessRuleEntity entity = template.findOne(query, UrlAccessRuleEntity.class);

            return Optional.ofNullable(entity).map(UrlAccessRuleEntity::getUrlId);
        });
    }

    @Override
    protected Class<UrlAccessRuleEntity> entityClass() {
        return UrlAccessRuleEntity.class;
    }

    @Override
    protected UrlAccessRuleEntity toEntity(UrlAccessRuleModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected UrlAccessRuleModel toModel(UrlAccessRuleEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    @Retry(name = "database")
    public UrlAccessRuleModel save(UrlAccessRuleModel model) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.save(mapper.toEntity(model))
        ));
    }

    @Override
    @Retry(name = "database")
    public UrlAccessRuleModel insert(UrlAccessRuleModel model) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(model))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlAccessRuleModel> findById(Long id) {
        return retryTranslator.execute(() -> repository.findById(id).map(mapper::toModel));
    }

    @Override
    @Retry(name = "database")
    public Page<UrlAccessRuleModel> findAll(UrlAccessRuleFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();

            applyBaseFilter(query, filter);

            QueryUtils.addEquals(query, "urlId", filter.getUrlId());
            QueryUtils.addEquals(query, "type", filter.getType());
            QueryUtils.addLike(query, "ruleValue", filter.getRuleValue());
            QueryUtils.addEquals(query, "active", filter.getActive());
            QueryUtils.addEquals(query, "assignedByUserId", filter.getAssignedByUserId());
            QueryUtils.addRange(query, "expiresAt", filter.getExpiresAtAfter(), filter.getExpiresAtBefore());

            return toPage(query, pageable);
        });
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return retryTranslator.execute(() -> super.existsById(id));
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return retryTranslator.execute(() -> super.deleteById(id));
    }

    @Override
    @Retry(name = "database")
    public List<UrlAccessRuleModel> findAllByUrlId(Long urlId) {
        return retryTranslator.execute(() -> repository
                .findAllByUrlIdAndActiveTrueAndExpiresAtAfter(urlId, LocalDateTime.now())
                .stream()
                .map(mapper::toModel)
                .toList());
    }

    @Override
    @Retry(name = "database")
    public boolean existsUnique(Long urlId, UrlAccessRuleTypeEnum type, String ruleValue) {
        return retryTranslator.execute(() -> repository.existsByUrlIdAndTypeAndRuleValue(urlId, type, ruleValue));
    }
}