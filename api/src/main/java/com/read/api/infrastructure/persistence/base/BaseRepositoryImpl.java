package com.read.api.infrastructure.persistence.base;

import com.mongodb.client.result.DeleteResult;
import com.read.api.api.dto.base.BaseFilter;
import com.read.api.domain.model.base.BaseModel;
import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import com.read.api.infrastructure.persistence.shared.MongoRetryTranslation;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public abstract class BaseRepositoryImpl<TModel extends BaseModel, TEntity extends BaseEntity, ID> {

    protected final MongoTemplate template;
    protected final MongoRetryTranslation retryTranslator;

    protected BaseRepositoryImpl(MongoTemplate template, MongoRetryTranslation retryTranslator) {
        this.template = template;
        this.retryTranslator = retryTranslator;
    }

    protected abstract Class<TEntity> entityClass();

    protected abstract TEntity toEntity(TModel model);

    protected abstract TModel toModel(TEntity entity);

    @Retry(name = "database")
    public boolean existsById(ID id) {
        return retryTranslator.execute(() -> {
            Query query = Query.query(Criteria.where("id").is(id));
            query.fields().include("_id");
            return template.exists(query, entityClass());
        });
    }

    @Retry(name = "database")
    public int deleteById(ID id) {
        return retryTranslator.execute(() -> {
            Query query = Query.query(Criteria.where("id").is(id));
            DeleteResult result = template.remove(query, entityClass());
            return (int) result.getDeletedCount();
        });
    }

    protected Page<TModel> toPage(Query query, Pageable pageable) {
        return retryTranslator.execute(() -> {
            query.with(pageable);

            List<TModel> content = template.find(query, entityClass())
                    .stream()
                    .map(this::toModel)
                    .toList();

            long total;
            long offset = pageable.getOffset();
            int pageSize = pageable.getPageSize();

            if (offset == 0 && content.size() < pageSize) {
                total = content.size();
            } else if (content.isEmpty() && offset > 0) {
                total = offset;
            } else {
                total = template.count(query.skip(-1).limit(-1), entityClass());
            }

            return new PageImpl<>(content, pageable, total);
        });
    }

    protected void applyBaseFilter(Query query, BaseFilter filter) {
        QueryUtils.addEquals(query, "id", filter.getId());
        QueryUtils.addRange(query, "createdAt", filter.getCreatedAtAfter(), filter.getCreatedAtBefore());
        QueryUtils.addRange(query, "updatedAt", filter.getUpdatedAtAfter(), filter.getUpdatedAtBefore());
    }

    @Retry(name = "database")
    public List<TModel> saveAll(List<TModel> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return retryTranslator.execute(() -> {
            BulkOperations bulk = template.bulkOps(
                    BulkOperations.BulkMode.UNORDERED,
                    entityClass()
            );

            List<TEntity> entities = models.stream()
                    .map(this::toEntity)
                    .toList();

            entities.forEach(entity -> {
                Query query = Query.query(Criteria.where("id").is(entity.getId()));

                FindAndReplaceOptions options = FindAndReplaceOptions.options().upsert();

                bulk.replaceOne(query, entity, options);
            });

            bulk.execute();

            return entities.stream()
                    .map(this::toModel)
                    .toList();
        });
    }

    @Retry(name = "database")
    public List<TModel> insertAll(List<TModel> models) {
        if (models == null || models.isEmpty()) {
            return List.of();
        }

        return retryTranslator.execute(() -> {
            BulkOperations bulk = template.bulkOps(
                    BulkOperations.BulkMode.UNORDERED,
                    entityClass()
            );

            List<TEntity> entities = models.stream()
                    .map(this::toEntity)
                    .toList();

            bulk.insert(entities);

            bulk.execute();

            return entities.stream()
                    .map(this::toModel)
                    .toList();
        });
    }

    @Retry(name = "database")
    public long deleteAll() {
        return retryTranslator.execute(() -> {
            DeleteResult result = template.remove(new Query(), entityClass());
            return result.getDeletedCount();
        });
    }
}