package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.ApiKeyRepositoryMapper;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
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

        int rows = dsl.insertInto(API_KEYS)
                .set(API_KEYS.ID, id)
                .set(API_KEYS.USER_ID, entity.getUserId())
                .set(API_KEYS.KEY_HASH, entity.getKeyHash())
                .set(API_KEYS.NAME, entity.getName())
                .set(API_KEYS.ACTIVE, entity.isActive())
                .set(API_KEYS.LAST_USED_AT, entity.getLastUsedAt())
                .set(API_KEYS.EXPIRES_AT, entity.getExpiresAt())
                .set(API_KEYS.CREATED_AT, now)
                .set(API_KEYS.UPDATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException("Failed to insert api key");
        }

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return entity;
    }

    @Override
    public ApiKeyModel save(ApiKeyModel entity) {

        entity.setUpdatedAt(LocalDateTime.now());

        int rows = dsl.update(API_KEYS)
                .set(API_KEYS.NAME, entity.getName())
                .set(API_KEYS.ACTIVE, entity.isActive())
                .set(API_KEYS.LAST_USED_AT, entity.getLastUsedAt())
                .set(API_KEYS.EXPIRES_AT, entity.getExpiresAt())
                .set(API_KEYS.UPDATED_AT, entity.getUpdatedAt())
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