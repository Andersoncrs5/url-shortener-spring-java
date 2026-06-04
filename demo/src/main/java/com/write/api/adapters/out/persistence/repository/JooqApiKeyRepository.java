package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.ApiKeyRepositoryMapper;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.ApiKeysRecord;
import com.write.api.ports.out.repository.IApiKeyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.API_KEYS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqApiKeyRepository implements IApiKeyRepository {

    DSLContext dsl;
    SnowflakeIdGenerator idGen;
    ApiKeyRepositoryMapper mapper;

    @Override
    public ApiKeyModel insert(ApiKeyModel entity) {

        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        ApiKeysRecord record = mapper.toRecord(entity);

        int rows = dsl.executeInsert(record);

        if (rows != 1) {
            throw new RuntimeException("Failed to insert api key");
        }

        return entity;
    }

    @Override
    public ApiKeyModel save(ApiKeyModel entity) {

        entity.setUpdatedAt(LocalDateTime.now());

        ApiKeysRecord record = mapper.toRecord(entity);

        int rows = dsl.update(API_KEYS)
                .set(record)
                .where(API_KEYS.ID.eq(entity.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException(
                    "ApiKey not found: " + entity.getId()
            );
        }

        if (rows > 1) {
            throw new IllegalStateException(
                    "More than one row affected"
            );
        }

        return entity;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(API_KEYS)
                .where(API_KEYS.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<ApiKeyModel> findById(Long id) {
        return dsl.selectFrom(API_KEYS)
                .where(API_KEYS.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(API_KEYS)
                        .where(API_KEYS.ID.eq(id))
        );
    }

    @Override
    public Optional<ApiKeyModel> findByKeyHash(String keyHash) {
        return dsl.selectFrom(API_KEYS)
                .where(API_KEYS.KEY_HASH.equalIgnoreCase(keyHash))
                .fetchOptional().map(mapper::toDomain);
    }
}