package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UrlRepositoryMapper;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlsRecord;
import com.write.api.ports.out.repository.IUrlRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.write.api.generated.jooq.tables.Urls.URLS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUrlRepository extends JooqRepository implements IUrlRepository {

    UrlRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public UrlModel save(UrlModel entity) {
        return execute(() -> {
            entity.setUpdatedAt(LocalDateTime.now());

            int rows = dsl.update(URLS)
                    .set(URLS.VERSION, entity.getVersion())
                    .set(URLS.USER_ID, entity.getUserId())
                    .set(URLS.SHORT_CODE, entity.getShortCode())
                    .set(URLS.DESCRIPTION, entity.getDescription())
                    .set(URLS.FAVICON_URL, entity.getFaviconUrl())
                    .set(URLS.ORIGINAL_URL, entity.getOriginalUrl())
                    .set(URLS.TITLE, entity.getTitle())
                    .set(URLS.DOMAIN, entity.getDomain())
                    .set(URLS.STATUS, entity.getStatus() != null ? entity.getStatus().name() : null)
                    .set(URLS.ACCESS_TYPE, entity.getAccessType() != null ? entity.getAccessType().name() : null)
                    .set(URLS.PASSWORD_HASH, entity.getPasswordHash())
                    .set(URLS.CUSTOM_ALIAS, entity.isCustomAlias())
                    .set(URLS.DELETED_AT, entity.getDeletedAt())
                    .set(URLS.EXPIRES_AT, entity.getExpiresAt())
                    .set(URLS.LAST_ACCESS_AT, entity.getLastAccessAt())
                    .set(URLS.UPDATED_AT, entity.getUpdatedAt())
                    .where(URLS.ID.eq(entity.getId()))
                    .execute();

            if (rows == 0) {
                throw new IllegalStateException("Url not found: " + entity.getId());
            }

            if (rows > 1) {
                throw new IllegalStateException("More than one row affected");
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public UrlModel insert(UrlModel entity) {
        return execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            int rows = dsl.insertInto(URLS)
                    .set(URLS.ID, id)
                    .set(URLS.VERSION, 1L)
                    .set(URLS.USER_ID, entity.getUserId())
                    .set(URLS.SHORT_CODE, entity.getShortCode())
                    .set(URLS.DESCRIPTION, entity.getDescription())
                    .set(URLS.FAVICON_URL, entity.getFaviconUrl())
                    .set(URLS.ORIGINAL_URL, entity.getOriginalUrl())
                    .set(URLS.TITLE, entity.getTitle())
                    .set(URLS.DOMAIN, entity.getDomain())
                    .set(URLS.STATUS, entity.getStatus() != null ? entity.getStatus().name() : null)
                    .set(URLS.ACCESS_TYPE, entity.getAccessType() != null ? entity.getAccessType().name() : null)
                    .set(URLS.PASSWORD_HASH, entity.getPasswordHash())
                    .set(URLS.CUSTOM_ALIAS, entity.isCustomAlias())
                    .set(URLS.DELETED_AT, entity.getDeletedAt())
                    .set(URLS.EXPIRES_AT, entity.getExpiresAt())
                    .set(URLS.LAST_ACCESS_AT, entity.getLastAccessAt())
                    .set(URLS.CREATED_AT, now)
                    .set(URLS.UPDATED_AT, now)
                    .execute();

            if (rows != 1) {
                throw new RuntimeException("Failed to insert url");
            }

            entity.setId(id);
            entity.setVersion(1L);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return execute(() ->
                dsl.delete(URLS)
                        .where(URLS.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlModel> findById(Long id) {
        return execute(() ->
                dsl.selectFrom(URLS)
                        .where(URLS.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URLS)
                                .where(URLS.ID.eq(id))
                )
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsByShortCode(String code) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URLS)
                                .where(URLS.SHORT_CODE.equalIgnoreCase(code))
                )
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsByUserIdAndUrlId(Long userId, Long urlId) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URLS)
                                .where(URLS.USER_ID.eq(userId))
                                .and(URLS.ID.eq(urlId))
                )
        );
    }

    @Override
    @Retry(name = "database")
    public List<UrlModel> findToDelete(UrlStatusEnum status, int limit, LocalDateTime deletedAt) {
        return execute(() -> {
            var records = dsl.selectFrom(URLS)
                    .where(URLS.STATUS.eq(status.name()))
                    .and(URLS.DELETED_AT.lessOrEqual(deletedAt))
                    .orderBy(URLS.DELETED_AT.asc(), URLS.ID.asc())
                    .limit(limit)
                    .fetch();

            if (records.isEmpty()) {
                return List.of();
            }

            return records
                    .stream()
                    .map(mapper::toDomain)
                    .toList();
        });
    }
}