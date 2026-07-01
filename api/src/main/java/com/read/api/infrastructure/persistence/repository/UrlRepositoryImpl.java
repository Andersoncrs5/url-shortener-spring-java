package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UrlEntity;
import com.read.api.infrastructure.persistence.mapper.UrlMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUrlRepository;
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

import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlRepositoryImpl
        extends BaseRepositoryImpl<UrlModel, UrlEntity, Long>
        implements UrlRepository {

    MongoUrlRepository repository;
    UrlMapperRepository mapper;

    public UrlRepositoryImpl(
            MongoTemplate template,
            UrlMapperRepository mapper,
            MongoUrlRepository repository,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Retry(name = "database")
    public UrlModel save(UrlModel url) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.save(mapper.toEntity(url))
        ));
    }

    @Override
    @Retry(name = "database")
    public UrlModel insert(UrlModel url) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(url))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlModel> findById(Long id) {
        return retryTranslator.execute(() -> repository.findById(id).map(mapper::toModel));
    }

    @Override
    @Retry(name = "database")
    public Page<UrlModel> findAll(UrlFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();

            applyBaseFilter(query, filter);

            QueryUtils.addEquals(query, "userId", filter.getUserId());
            QueryUtils.addEquals(query, "shortCode", filter.getShortCode());
            QueryUtils.addLike(query, "description", filter.getDescription());
            QueryUtils.addLike(query, "faviconUrl", filter.getFaviconUrl());
            QueryUtils.addLike(query, "originalUrl", filter.getOriginalUrl());
            QueryUtils.addLike(query, "title", filter.getTitle());
            QueryUtils.addLike(query, "domain", filter.getDomain());

            QueryUtils.addEquals(query, "status", filter.getStatus());
            QueryUtils.addEquals(query, "accessType", filter.getAccessType());
            QueryUtils.addEquals(query, "passwordHash", filter.getPasswordHash());
            QueryUtils.addEquals(query, "customAlias", filter.getCustomAlias());

            QueryUtils.addRange(query, "deletedAt", filter.getDeletedAtMin(), filter.getDeletedAtMax());
            QueryUtils.addRange(query, "expiresAt", filter.getExpiresAtMin(), filter.getExpiresAtMax());
            QueryUtils.addRange(query, "lastAccessAt", filter.getLastAccessAtMin(), filter.getLastAccessAtMax());

            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                if (filter.isMatchAllTags()) {
                    query.addCriteria(Criteria.where("tags").all(filter.getTags()));
                } else {
                    query.addCriteria(Criteria.where("tags").in(filter.getTags()));
                }
            }

            return toPage(query, pageable);
        });
    }

    @Override
    protected Class<UrlEntity> entityClass() {
        return UrlEntity.class;
    }

    @Override
    protected UrlEntity toEntity(UrlModel urlModel) {
        return mapper.toEntity(urlModel);
    }

    @Override
    protected UrlModel toModel(UrlEntity urlEntity) {
        return mapper.toModel(urlEntity);
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlModel> findByShortCode(String code) {
        return retryTranslator.execute(() -> repository.findByShortCode(code).map(this::toModel));
    }
}