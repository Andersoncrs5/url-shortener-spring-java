package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UrlAccessRuleRepositoryMapper;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlAccessRuleRecord;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import io.github.resilience4j.retry.annotation.Retry;
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
public class JooqUrlAccessRuleRepository
        extends JooqRepository
        implements IUrlAccessRuleRepository {

    UrlAccessRuleRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public UrlAccessRuleModel save(UrlAccessRuleModel entity) {
        return execute(() -> {

            entity.setUpdatedAt(LocalDateTime.now());

            UrlAccessRuleRecord record = mapper.toRecord(entity);

            int rows = dsl.executeUpdate(record);

            if (rows == 0) {
                throw new IllegalStateException(
                        "UrlAccessRule not found: " + entity.getId()
                );
            }

            if (rows > 1) {
                throw new IllegalStateException(
                        "More than one row affected"
                );
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public UrlAccessRuleModel insert(UrlAccessRuleModel entity) {

        return execute(() -> {

            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            entity.setId(id);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            UrlAccessRuleRecord record = mapper.toRecord(entity);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException(
                        "Failed to insert url access rule"
                );
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {

        return execute(() ->
                dsl.delete(URL_ACCESS_RULE)
                        .where(URL_ACCESS_RULE.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlAccessRuleModel> findById(Long id) {

        return execute(() ->
                dsl.selectFrom(URL_ACCESS_RULE)
                        .where(URL_ACCESS_RULE.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {

        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URL_ACCESS_RULE)
                                .where(URL_ACCESS_RULE.ID.eq(id))
                )
        );
    }
}