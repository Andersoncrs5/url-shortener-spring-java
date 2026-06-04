package com.write.api.adapters.out.persistence.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.out.persistence.mapper.JooqOutboxEventRepositoryMapper;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.OutboxEventsRecord;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.jooq.JSON;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.write.api.generated.jooq.tables.OutboxEvents.OUTBOX_EVENTS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqOutboxEventRepository implements IOutboxEventRepository {

    DSLContext dsl;
    SnowflakeIdGenerator generator;
    ObjectMapper objectMapper;
    JooqOutboxEventRepositoryMapper mapper;

    @Override
    public OutboxEventModel save(OutboxEventModel entity) {

        if (entity.getId() == null) {
            return insert(entity);
        }

        JSON payload = JSON.json(entity.getPayload());

        int rows = dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.AGGREGATE_TYPE, enumName(entity.getAggregateType()))
                .set(OUTBOX_EVENTS.AGGREGATE_ID, entity.getAggregateId())
                .set(OUTBOX_EVENTS.EVENT_TYPE, enumName(entity.getEventType()))
                .set(OUTBOX_EVENTS.PAYLOAD, payload)
                .set(OUTBOX_EVENTS.STATUS, enumName(entity.getStatus()))
                .set(OUTBOX_EVENTS.RETRY_COUNT, entity.getRetryCount())
                .set(OUTBOX_EVENTS.TOPIC, enumName(entity.getTopic()))
                .set(OUTBOX_EVENTS.ERROR_MESSAGE, entity.getErrorMessage())
                .set(OUTBOX_EVENTS.NEXT_RETRY_AT, entity.getNextRetryAt())
                .set(OUTBOX_EVENTS.PROCESSED_AT, entity.getProcessedAt())
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.ID.eq(entity.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException("Outbox event not found: " + entity.getId());
        }

        if (rows > 1) {
            throw new IllegalStateException("More than one row affected");
        }

        return entity;
    }

    @Override
    public OutboxEventModel insert(OutboxEventModel entity) {
        entity.setId(generator.nextId());
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        OutboxEventsRecord record = mapper.toRecord(entity);

        int rows = dsl.executeInsert(record);
        if (rows != 1) {
            throw new IllegalStateException("Failed to insert outbox event");
        }

        return entity;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<OutboxEventModel> findById(Long id) {
        return dsl.selectFrom(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(OUTBOX_EVENTS, OUTBOX_EVENTS.ID.eq(id));
    }

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private AggregateTypeEnum toAggregateType(String value) {
        return value == null ? null : AggregateTypeEnum.valueOf(value);
    }

    private EventTypeEnum toEventType(String value) {
        return value == null ? null : EventTypeEnum.valueOf(value);
    }

    private OutboxStatusEnum toStatus(String value) {
        return value == null ? null : OutboxStatusEnum.valueOf(value);
    }

    public List<OutboxEventModel> claimPending(int limit) {
        return dsl.transactionResult(configuration -> {
            DSLContext tx = org.jooq.impl.DSL.using(configuration);

            var records = tx.selectFrom(OUTBOX_EVENTS)
                    .where(OUTBOX_EVENTS.STATUS.eq(OutboxStatusEnum.PENDING.name()))
                    .orderBy(OUTBOX_EVENTS.CREATED_AT.asc(), OUTBOX_EVENTS.ID.asc())
                    .limit(limit)
                    .forUpdate()
                    .fetch();

            if (records.isEmpty()) {
                return List.of();
            }

            List<OutboxEventModel> events = records
                    .stream()
                    .map(mapper::toDomain)
                    .toList();

            tx.update(OUTBOX_EVENTS)
                    .set(OUTBOX_EVENTS.STATUS, OutboxStatusEnum.PROCESSING.name())
                    .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                    .where(OUTBOX_EVENTS.ID.in(
                            events.stream()
                                    .map(OutboxEventModel::getId)
                                    .toList()
                    ))
                    .and(OUTBOX_EVENTS.STATUS.eq(OutboxStatusEnum.PENDING.name()))
                    .execute();

            events.forEach(e -> e.setStatus(OutboxStatusEnum.PROCESSING));

            return events;
        });
    }

    @Override
    public int markProcessed(Long id) {
        return dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.STATUS, OutboxStatusEnum.PROCESSED.name())
                .set(OUTBOX_EVENTS.PROCESSED_AT, LocalDateTime.now())
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.ID.eq(id))
                .execute();
    }

    @Override
    public int markFailed(Long id, String errorMessage, LocalDateTime nextRetryAt) {
        return dsl.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.STATUS, OutboxStatusEnum.FAILED.name())
                .set(OUTBOX_EVENTS.ERROR_MESSAGE, errorMessage)
                .set(OUTBOX_EVENTS.NEXT_RETRY_AT, nextRetryAt)
                .set(OUTBOX_EVENTS.UPDATED_AT, LocalDateTime.now())
                .where(OUTBOX_EVENTS.ID.eq(id))
                .execute();
    }
}