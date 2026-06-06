package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.JooqOutboxEventRepositoryMapper;
import com.write.api.core.domain.enums.OutboxStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.generated.jooq.tables.records.OutboxEventsRecord;
import com.write.api.ports.out.repository.IOutboxEventRepository;
import io.github.resilience4j.retry.annotation.Retry;
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
public class JooqOutboxEventRepository extends JooqRepository implements IOutboxEventRepository {

    JooqOutboxEventRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
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
    @Retry(name = "database")
    public OutboxEventModel insert(OutboxEventModel entity) {

        return retryTranslator.execute(() -> {

            entity.setId(idGen.nextId());

            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            OutboxEventsRecord record = mapper.toRecord(entity);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new IllegalStateException(
                        "Failed to insert outbox event"
                );
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return dsl.delete(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.ID.eq(id))
                .execute();
    }

    @Override
    @Retry(name = "database")
    public Optional<OutboxEventModel> findById(Long id) {
        return dsl.selectFrom(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    @Retry(name = "database")
    public Optional<OutboxEventModel> findByAggregateId(Long aggregateId) {
        return dsl.selectFrom(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.AGGREGATE_ID.eq(aggregateId))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return dsl.fetchExists(OUTBOX_EVENTS, OUTBOX_EVENTS.ID.eq(id));
    }

    private String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }

    @Retry(name = "database")
    public List<OutboxEventModel> findByStatus(
            OutboxStatusEnum status,
            int limit
    ) {
        return dsl.selectFrom(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.STATUS.eq(status.name()))
                .orderBy(
                        OUTBOX_EVENTS.CREATED_AT.asc(),
                        OUTBOX_EVENTS.ID.asc()
                )
                .limit(limit)
                .fetch(mapper::toDomain);
    }

    @Override
    public List<OutboxEventModel> saveAll(List<OutboxEventModel> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        LocalDateTime now = LocalDateTime.now();

        List<OutboxEventsRecord> records = items.stream()
                .peek(item -> item.setUpdatedAt(now))
                .map(mapper::toRecord)
                .toList();

        int[] rows = dsl.batch(
                records.stream()
                        .map(r -> dsl.update(OUTBOX_EVENTS)
                                .set(OUTBOX_EVENTS.AGGREGATE_TYPE, r.getAggregateType())
                                .set(OUTBOX_EVENTS.AGGREGATE_ID, r.getAggregateId())
                                .set(OUTBOX_EVENTS.EVENT_TYPE, r.getEventType())
                                .set(OUTBOX_EVENTS.PAYLOAD, r.getPayload())
                                .set(OUTBOX_EVENTS.STATUS, r.getStatus())
                                .set(OUTBOX_EVENTS.RETRY_COUNT, r.getRetryCount())
                                .set(OUTBOX_EVENTS.TOPIC, r.getTopic())
                                .set(OUTBOX_EVENTS.ERROR_MESSAGE, r.getErrorMessage())
                                .set(OUTBOX_EVENTS.NEXT_RETRY_AT, r.getNextRetryAt())
                                .set(OUTBOX_EVENTS.PROCESSED_AT, r.getProcessedAt())
                                .set(OUTBOX_EVENTS.UPDATED_AT, r.getUpdatedAt())
                                .where(OUTBOX_EVENTS.ID.eq(r.getId())))
                        .toArray(org.jooq.Query[]::new)
        ).execute();

        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == 0) {
                throw new IllegalStateException("Outbox event not found: " + items.get(i).getId());
            }
        }

        return items;
    }
}