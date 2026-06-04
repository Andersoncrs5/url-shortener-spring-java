package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlRepositoryMapper;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlsRecord;
import com.write.api.ports.out.repository.IUrlRepository;
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
public class JooqUrlRepository implements IUrlRepository {

    DSLContext dsl;
    UrlRepositoryMapper mapper;
    SnowflakeIdGenerator idGen;

    @Override
    public UrlModel save(UrlModel entity) {
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
    }

    @Override
    public UrlModel insert(UrlModel entity) {
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
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(URLS)
                .where(URLS.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<UrlModel> findById(Long id) {
        return dsl.selectFrom(URLS)
                .where(URLS.ID.eq(id))
                .fetchOptional()
                .map(this::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(URLS)
                        .where(URLS.ID.eq(id))
        );
    }

    @Override
    public boolean existsByShortCode(String code) {
        return dsl.fetchExists(
                dsl.selectFrom(URLS)
                        .where(URLS.SHORT_CODE.equalIgnoreCase(code))
        );
    }

    @Override
    public boolean existsByUserIdAndUrlId(Long userId, Long urlId) {
        return dsl.fetchExists(
                dsl.selectFrom(URLS)
                        .where(URLS.USER_ID.eq(userId))
                        .and(URLS.ID.eq(urlId))
        );
    }

    @Override
    public List<UrlModel> findToDelete(UrlStatusEnum status, int limit, LocalDateTime deletedAt) {
    var records = dsl.selectFrom(URLS)
            .where(URLS.STATUS.eq(status.name()))
            .and(URLS.DELETED_AT.lessOrEqual(deletedAt))
            .limit(limit)
            .fetch();

        if (records.isEmpty()) {
            return List.of();
        }

        return records
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    private UrlModel toDomain(UrlsRecord record) {
        UrlModel model = new UrlModel();

        model.setId(record.getId());
        model.setVersion(record.getVersion());
        model.setUserId(record.getUserId());
        model.setShortCode(record.getShortCode());
        model.setDescription(record.getDescription());
        model.setFaviconUrl(record.getFaviconUrl());
        model.setOriginalUrl(record.getOriginalUrl());
        model.setTitle(record.getTitle());
        model.setDomain(record.getDomain());
        model.setStatus(record.getStatus() != null ? UrlStatusEnum.valueOf(record.getStatus()) : null);
        model.setAccessType(record.getAccessType() != null ? UrlAccessTypeEnum.valueOf(record.getAccessType()) : null);
        model.setPasswordHash(record.getPasswordHash());
        model.setCustomAlias(Boolean.TRUE.equals(record.getCustomAlias()));
        model.setDeletedAt(record.getDeletedAt());
        model.setExpiresAt(record.getExpiresAt());
        model.setLastAccessAt(record.getLastAccessAt());
        model.setCreatedAt(record.getCreatedAt());
        model.setUpdatedAt(record.getUpdatedAt());

        return model;
    }
}