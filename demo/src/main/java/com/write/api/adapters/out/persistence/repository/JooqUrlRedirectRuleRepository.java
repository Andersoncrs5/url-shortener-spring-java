package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UrlRedirectRuleRepositoryMapper;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.generated.jooq.tables.records.UrlRedirectRulesRecord;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.URL_REDIRECT_RULES;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUrlRedirectRuleRepository
        extends JooqRepository
        implements IUrlRedirectRuleRepository {

    UrlRedirectRuleRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public UrlRedirectRuleModel save(UrlRedirectRuleModel entity) {
        return execute(() -> {
            entity.setUpdatedAt(LocalDateTime.now());

            UrlRedirectRulesRecord record = mapper.toRecord(entity);

            int rows = dsl.executeUpdate(record);

            if (rows == 0) {
                throw new IllegalStateException(
                        "UrlRedirectRule not found: " + entity.getId()
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
    public UrlRedirectRuleModel insert(UrlRedirectRuleModel entity) {
        return execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            entity.setId(id);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            UrlRedirectRulesRecord record = mapper.toRecord(entity);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException(
                        "Failed to insert url redirect rule"
                );
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return execute(() ->
                dsl.delete(URL_REDIRECT_RULES)
                        .where(URL_REDIRECT_RULES.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlRedirectRuleModel> findById(Long id) {
        return execute(() ->
                dsl.selectFrom(URL_REDIRECT_RULES)
                        .where(URL_REDIRECT_RULES.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URL_REDIRECT_RULES)
                                .where(URL_REDIRECT_RULES.ID.eq(id))
                )
        );
    }

    @Override
    public int countByUrlId(Long urlId) {
            return execute(
                    () -> dsl.selectCount().from(URL_REDIRECT_RULES).where(URL_REDIRECT_RULES.URL_ID.eq(urlId)).execute()
            );
    }
}