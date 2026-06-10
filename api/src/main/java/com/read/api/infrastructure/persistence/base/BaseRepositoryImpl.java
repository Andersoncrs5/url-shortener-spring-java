package com.read.api.infrastructure.persistence.base;


import com.mongodb.client.result.DeleteResult;
import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.model.base.BaseModel;
import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public abstract class BaseRepositoryImpl<
        TModel extends BaseModel,
        TEntity extends BaseEntity,
        ID> {

    protected final MongoTemplate template;

    protected BaseRepositoryImpl(MongoTemplate template) {
        this.template = template;
    }

    protected abstract Class<TEntity> entityClass();

    protected abstract TEntity toEntity(TModel model);

    protected abstract TModel toModel(TEntity entity);

    public boolean existsById(ID id) {

        Query query = Query.query(
                Criteria.where("id").is(id)
        );

        return template.exists(
                query,
                entityClass()
        );
    }

    public int deleteById(ID id) {

        Query query = Query.query(
                Criteria.where("id").is(id)
        );

        DeleteResult result =
                template.remove(
                        query,
                        entityClass()
                );

        return (int) result.getDeletedCount();
    }

    protected Page<TModel> toPage(
            Query query,
            Pageable pageable
    ) {

        long total =
                template.count(
                        query,
                        entityClass()
                );

        query.with(pageable);

        List<TModel> content =
                template.find(
                                query,
                                entityClass()
                        )
                        .stream()
                        .map(this::toModel)
                        .toList();

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    protected void applyBaseFilter(
            Query query,
            BaseFilter filter
    ) {

        QueryUtils.addEquals(
                query,
                "id",
                filter.getId()
        );

        QueryUtils.addRange(
                query,
                "createdAt",
                filter.getCreatedAtAfter(),
                filter.getCreatedAtBefore()
        );

        QueryUtils.addRange(
                query,
                "updatedAt",
                filter.getUpdatedAtAfter(),
                filter.getUpdatedAtBefore()
        );
    }

    protected List<TModel> saveAll(
            List<TModel> models
    ) {

        List<TEntity> entities = models.stream()
                .map(this::toEntity)
                .toList();

        return template.insert(entities, entityClass())
                .stream()
                .map(this::toModel)
                .toList();
    }

    protected List<TModel> insertAll(
            List<TModel> models
    ) {

        List<TEntity> entities = models.stream()
                .map(this::toEntity)
                .toList();

        return template.insert(entities, entityClass())
                .stream()
                .map(this::toModel)
                .toList();
    }

    protected long deleteAll() {

        DeleteResult result =
                template.remove(
                        new Query(),
                        entityClass()
                );

        return result.getDeletedCount();
    }
}