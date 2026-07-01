package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UrlRedirectRuleEntity;
import com.read.api.infrastructure.persistence.mapper.UrlRedirectRuleMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUrlRedirectRuleRepository;
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
public class UrlRedirectRuleRepositoryImpl
        extends BaseRepositoryImpl<UrlRedirectRuleModel, UrlRedirectRuleEntity, Long>
        implements UrlRedirectRuleRepository {

    UrlRedirectRuleMapperRepository mapper;
    MongoUrlRedirectRuleRepository repository;

    public UrlRedirectRuleRepositoryImpl(
            MongoTemplate template,
            UrlRedirectRuleMapperRepository mapper,
            MongoUrlRedirectRuleRepository repository,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    @Retry(name = "database")
    public List<UrlRedirectRuleModel> findActiveRulesByUrlId(Long urlId) {
        return retryTranslator.execute(() -> {
            LocalDateTime now = LocalDateTime.now();
            Query query = new Query();

            QueryUtils.addEquals(query, "urlId", urlId);
            QueryUtils.addEquals(query, "active", true);

            Criteria startAtCriteria = new Criteria().orOperator(
                    Criteria.where("startAt").exists(false),
                    Criteria.where("startAt").lte(now)
            );

            Criteria endAtCriteria = new Criteria().orOperator(
                    Criteria.where("endAt").exists(false),
                    Criteria.where("endAt").gte(now)
            );

            query.addCriteria(new Criteria().andOperator(startAtCriteria, endAtCriteria));

            return template.find(query, UrlRedirectRuleEntity.class)
                    .stream()
                    .map(mapper::toModel)
                    .toList();
        });
    }

    @Override
    @Retry(name = "database")
    public Optional<Long> findUrlIdById(Long id) {
        return retryTranslator.execute(() -> {
            Query query = Query.query(Criteria.where("id").is(id));
            query.fields().include("urlId");

            UrlRedirectRuleEntity entity = template.findOne(query, UrlRedirectRuleEntity.class);

            return Optional.ofNullable(entity).map(UrlRedirectRuleEntity::getUrlId);
        });
    }

    @Override
    @Retry(name = "database")
    public Page<UrlRedirectRuleModel> findAll(UrlRedirectRuleFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();

            applyBaseFilter(query, filter);

            QueryUtils.addEquals(query, "urlId", filter.getUrlId());
            QueryUtils.addLike(query, "countryCode", filter.getCountryCode());
            QueryUtils.addLike(query, "region", filter.getRegion());
            QueryUtils.addEquals(query, "continent", filter.getContinent());
            QueryUtils.addEquals(query, "os", filter.getOs());
            QueryUtils.addEquals(query, "browser", filter.getBrowser());
            QueryUtils.addEquals(query, "matchType", filter.getMatchType());
            QueryUtils.addLike(query, "redirectUrl", filter.getRedirectUrl());
            QueryUtils.addLike(query, "ruleHash", filter.getRuleHash());
            QueryUtils.addEquals(query, "priority", filter.getPriority());
            QueryUtils.addEquals(query, "active", filter.getActive());

            QueryUtils.addRange(query, "startAt", filter.getStartAtAfter(), filter.getStartAtBefore());
            QueryUtils.addRange(query, "endAt", filter.getEndAtAfter(), filter.getEndAtBefore());

            return toPage(query, pageable);
        });
    }

    @Override
    protected Class<UrlRedirectRuleEntity> entityClass() {
        return UrlRedirectRuleEntity.class;
    }

    @Override
    protected UrlRedirectRuleEntity toEntity(UrlRedirectRuleModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected UrlRedirectRuleModel toModel(UrlRedirectRuleEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    @Retry(name = "database")
    public UrlRedirectRuleModel save(UrlRedirectRuleModel model) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.save(mapper.toEntity(model))
        ));
    }

    @Override
    @Retry(name = "database")
    public UrlRedirectRuleModel insert(UrlRedirectRuleModel model) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(model))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlRedirectRuleModel> findById(Long id) {
        return retryTranslator.execute(() -> repository.findById(id).map(mapper::toModel));
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
}