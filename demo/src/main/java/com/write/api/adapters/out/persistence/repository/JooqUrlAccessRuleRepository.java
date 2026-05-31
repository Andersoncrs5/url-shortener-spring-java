package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlAccessRuleRepositoryMapper;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.URL_ACCESS_RULE;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUrlAccessRuleRepository implements IUrlAccessRuleRepository {

    DSLContext dsl;
    SnowflakeIdGenerator idGen;
    UrlAccessRuleRepositoryMapper mapper;

    @Override
    public UrlAccessRuleModel save(UrlAccessRuleModel entity) {
        int rows = dsl.update(URL_ACCESS_RULE)
                .set(URL_ACCESS_RULE.URL_ID, entity.getUrlId())
                .set(URL_ACCESS_RULE.TYPE,
                        entity.getType() != null ? entity.getType().name() : null)
                .set(URL_ACCESS_RULE.RULE_VALUE, entity.getRuleValue())
                .set(URL_ACCESS_RULE.ACTIVE, entity.isActive())
                .set(URL_ACCESS_RULE.ASSIGNED_BY_USER_ID, entity.getAssignedByUserId())
                .set(URL_ACCESS_RULE.EXPIRES_AT, entity.getExpiresAt())
                .set(URL_ACCESS_RULE.UPDATED_AT, LocalDateTime.now())
                .where(URL_ACCESS_RULE.ID.eq(entity.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException("UrlAccessRule not found: " + entity.getId());
        }

        if (rows > 1) {
            throw new IllegalStateException("More than one row affected");
        }

        return entity;
    }

    @Override
    public UrlAccessRuleModel insert(UrlAccessRuleModel entity) {
        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        int rows = dsl.insertInto(URL_ACCESS_RULE)
                .set(URL_ACCESS_RULE.ID, id)
                .set(URL_ACCESS_RULE.URL_ID, entity.getUrlId())
                .set(URL_ACCESS_RULE.TYPE,
                        entity.getType() != null ? entity.getType().name() : null)
                .set(URL_ACCESS_RULE.RULE_VALUE, entity.getRuleValue())
                .set(URL_ACCESS_RULE.ACTIVE, entity.isActive())
                .set(URL_ACCESS_RULE.ASSIGNED_BY_USER_ID, entity.getAssignedByUserId())
                .set(URL_ACCESS_RULE.EXPIRES_AT, entity.getExpiresAt())
                .set(URL_ACCESS_RULE.CREATED_AT, now)
                .set(URL_ACCESS_RULE.UPDATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException("Failed to insert url access rule");
        }

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return entity;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(URL_ACCESS_RULE)
                .where(URL_ACCESS_RULE.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<UrlAccessRuleModel> findById(Long id) {
        return dsl.selectFrom(URL_ACCESS_RULE)
                .where(URL_ACCESS_RULE.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(URL_ACCESS_RULE)
                        .where(URL_ACCESS_RULE.ID.eq(id))
        );
    }
}