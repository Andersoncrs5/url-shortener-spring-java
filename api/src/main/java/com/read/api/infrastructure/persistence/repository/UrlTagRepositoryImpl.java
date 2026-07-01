package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.UrlTagEntity;
import com.read.api.infrastructure.persistence.mapper.UrlTagMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUrlTagRepository;
import com.read.api.infrastructure.persistence.shared.MongoRetryTranslation;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlTagRepositoryImpl
        extends BaseRepositoryImpl<UrlTagModel, UrlTagEntity, Long>
        implements UrlTagRepository {

    UrlTagMapperRepository mapper;
    MongoUrlTagRepository repository;

    public UrlTagRepositoryImpl(
            MongoTemplate template,
            UrlTagMapperRepository mapper,
            MongoUrlTagRepository repository,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected Class<UrlTagEntity> entityClass() {
        return UrlTagEntity.class;
    }

    @Override
    protected UrlTagEntity toEntity(UrlTagModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected UrlTagModel toModel(UrlTagEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    @Retry(name = "database")
    public boolean existsByName(String name) {
        return retryTranslator.execute(() -> repository.existsByName(name));
    }

    @Override
    @Retry(name = "database")
    public boolean existsBySlug(String slug) {
        return retryTranslator.execute(() -> repository.existsBySlug(slug));
    }

    @Override
    @Retry(name = "database")
    public UrlTagModel save(UrlTagModel model) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.save(mapper.toEntity(model))
        ));
    }

    @Override
    @Retry(name = "database")
    public UrlTagModel insert(UrlTagModel model) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(model))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlTagModel> findById(Long id) {
        return retryTranslator.execute(() -> repository.findById(id).map(mapper::toModel));
    }

    @Override
    @Retry(name = "database")
    public Page<UrlTagModel> findAll(UrlTagFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();

            applyBaseFilter(query, filter);

            QueryUtils.addEquals(query, "userId", filter.getUserId());
            QueryUtils.addLike(query, "name", filter.getName());
            QueryUtils.addLike(query, "slug", filter.getSlug());
            QueryUtils.addEquals(query, "color", filter.getColor());
            QueryUtils.addLike(query, "description", filter.getDescription());
            QueryUtils.addEquals(query, "parentId", filter.getParentId());
            QueryUtils.addEquals(query, "active", filter.getActive());

            return toPage(query, pageable);
        });
    }
}