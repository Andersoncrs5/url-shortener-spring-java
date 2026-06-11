package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.outbox.OutboxEventFilter;
import com.read.api.domain.model.OutboxEventModel;
import com.read.api.domain.repository.OutboxEventRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.OutboxEventEntity;
import com.read.api.infrastructure.persistence.mapper.OutboxMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoOutboxEventRepository;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
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
public class OutboxEventRepositoryImpl
        extends BaseRepositoryImpl<
                OutboxEventModel,
                OutboxEventEntity,
                Long>
        implements OutboxEventRepository {

    OutboxMapperRepository mapper;
    MongoOutboxEventRepository repository;

    public OutboxEventRepositoryImpl(
            MongoTemplate template,
            OutboxMapperRepository mapper,
            MongoOutboxEventRepository repository
    ) {
        super(template);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected Class<OutboxEventEntity> entityClass() {
        return OutboxEventEntity.class;
    }

    @Override
    protected OutboxEventEntity toEntity(
            OutboxEventModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected OutboxEventModel toModel(OutboxEventEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    public OutboxEventModel save(OutboxEventModel event) {
        return mapper.toModel(repository.save(mapper.toEntity(event)));
    }

    @Override
    public OutboxEventModel insert(OutboxEventModel event) {
        return mapper.toModel(repository.insert(mapper.toEntity(event)));
    }

    @Override
    public Optional<OutboxEventModel> findById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return super.existsById(id);
    }

    @Override
    public int deleteById(Long id) {
        return super.deleteById(id);
    }

    @Override
    public Page<OutboxEventModel> findAll(
            OutboxEventFilter filter,
            Pageable pageable
    ) {

        Query query = new Query();

        applyBaseFilter(query, filter);

        QueryUtils.addEquals(
                query,
                "aggregateType",
                filter.getAggregateType()
        );

        QueryUtils.addEquals(
                query,
                "aggregateId",
                filter.getAggregateId()
        );

        QueryUtils.addEquals(
                query,
                "eventType",
                filter.getEventType()
        );

        QueryUtils.addEquals(
                query,
                "topic",
                filter.getTopic()
        );

        QueryUtils.addEquals(
                query,
                "status",
                filter.getStatus()
        );

        QueryUtils.addLike(
                query,
                "payload",
                filter.getPayload()
        );

        QueryUtils.addLike(
                query,
                "errorMessage",
                filter.getErrorMessage()
        );

        QueryUtils.addRange(
                query,
                "retryCount",
                filter.getRetryCountMin(),
                filter.getRetryCountMax()
        );

        QueryUtils.addRange(
                query,
                "nextRetryAt",
                filter.getNextRetryAtAfter(),
                filter.getNextRetryAtBefore()
        );

        QueryUtils.addRange(
                query,
                "processedAt",
                filter.getProcessedAtAfter(),
                filter.getProcessedAtBefore()
        );

        return toPage(
                query,
                pageable
        );
    }
}