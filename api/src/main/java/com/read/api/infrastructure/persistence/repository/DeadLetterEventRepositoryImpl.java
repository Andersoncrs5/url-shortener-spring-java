package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.DeadLetterEventEntity;
import com.read.api.infrastructure.persistence.mapper.DeadLetterEventMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoDeadLetterEventRepository;
import com.read.api.infrastructure.persistence.shared.MongoRetryTranslation;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeadLetterEventRepositoryImpl
        extends BaseRepositoryImpl<DeadLetterEventModel, DeadLetterEventEntity, Long>
        implements DeadLetterEventRepository {

    DeadLetterEventMapperRepository mapper;
    MongoDeadLetterEventRepository repository;

    public DeadLetterEventRepositoryImpl(
            MongoTemplate template,
            DeadLetterEventMapperRepository mapper,
            MongoDeadLetterEventRepository repository,
            MongoRetryTranslation retryTranslator
    ) {
        super(template, retryTranslator);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected Class<DeadLetterEventEntity> entityClass() {
        return DeadLetterEventEntity.class;
    }

    @Override
    protected DeadLetterEventEntity toEntity(DeadLetterEventModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected DeadLetterEventModel toModel(DeadLetterEventEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    @Retry(name = "database")
    public DeadLetterEventModel save(DeadLetterEventModel event) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.save(mapper.toEntity(event))
        ));
    }

    @Override
    @Retry(name = "database")
    public DeadLetterEventModel insert(DeadLetterEventModel event) {
        return retryTranslator.execute(() -> mapper.toModel(
                repository.insert(mapper.toEntity(event))
        ));
    }

    @Override
    @Retry(name = "database")
    public Optional<DeadLetterEventModel> findById(Long id) {
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

    @Override
    @Retry(name = "database")
    public Page<DeadLetterEventModel> findAll(DeadLetterEventFilter filter, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();
            applyBaseFilter(query, filter);

            QueryUtils.addEquals(query, "eventId", filter.getEventId());
            QueryUtils.addEquals(query, "sourceTopic", filter.getSourceTopic());
            QueryUtils.addEquals(query, "targetDlqTopic", filter.getTargetDlqTopic());
            QueryUtils.addEquals(query, "eventType", filter.getEventType());
            QueryUtils.addEquals(query, "status", filter.getStatus());

            QueryUtils.addLike(query, "errorMessage", filter.getErrorMessage());
            QueryUtils.addLike(query, "stackTrace", filter.getStackTrace());

            QueryUtils.addRange(query, "retryCount", filter.getRetryCountMin(), filter.getRetryCountMax());
            QueryUtils.addRange(query, "maxRetries", filter.getMaxRetriesMin(), filter.getMaxRetriesMax());
            QueryUtils.addRange(query, "lastRetryAt", filter.getLastRetryAtMin(), filter.getLastRetryAtMax());
            QueryUtils.addRange(query, "resolvedAt", filter.getResolvedAtMin(), filter.getResolvedAtMax());
            QueryUtils.addRange(query, "nextRetryAt", filter.getNextRetryAtMin(), filter.getNextRetryAtMax());

            return toPage(query, pageable);
        });
    }

    @Override
    @Retry(name = "database")
    public Optional<DeadLetterEventModel> findByEventId(Long id) {
        return retryTranslator.execute(() -> repository.findByEventId(id).map(mapper::toModel));
    }

    @Override
    @Retry(name = "database")
    public List<DeadLetterEventModel> findTop100ByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
            DeadLetterStatus status,
            Integer maxRetries
    ) {
        return retryTranslator.execute(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("status").is(status));
            query.addCriteria(Criteria.where("retryCount").lt(maxRetries));
            query.limit(100);
            query.with(Sort.by(Sort.Direction.ASC, "createdAt"));

            return template.find(query, entityClass()).stream()
                    .map(this::toModel)
                    .toList();
        });
    }

    @Override
    @Retry(name = "database")
    public List<DeadLetterEventModel> findPendingRetryEvents(LocalDateTime now, Pageable pageable) {
        return retryTranslator.execute(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("status").is(DeadLetterStatus.PENDING));
            query.addCriteria(Criteria.where("nextRetryAt").lte(now));
            query.with(Sort.by(Sort.Direction.ASC, "createdAt"));
            query.with(pageable);

            return template.find(query, entityClass()).stream()
                    .map(this::toModel)
                    .toList();
        });
    }
}