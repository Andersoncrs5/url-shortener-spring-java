package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlRedirectRuleRepositoryMapper;
import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.enums.UrlRedirectRulesBrowser;
import com.write.api.generated.jooq.enums.UrlRedirectRulesContinent;
import com.write.api.generated.jooq.enums.UrlRedirectRulesMatchType;
import com.write.api.generated.jooq.enums.UrlRedirectRulesOs;
import com.write.api.generated.jooq.tables.records.UrlRedirectRulesRecord;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.URL_REDIRECT_RULES;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUrlRedirectRuleRepository implements IUrlRedirectRuleRepository {

    DSLContext dsl;
    SnowflakeIdGenerator idGen;
    UrlRedirectRuleRepositoryMapper mapper;

    @Override
    public UrlRedirectRuleModel save(UrlRedirectRuleModel entity) {
        entity.setUpdatedAt(LocalDateTime.now());

        UrlRedirectRulesRecord record = mapper.toRecord(entity);

        int rows = dsl.executeUpdate(record);

        if (rows == 0) {
            throw new IllegalStateException("UrlRedirectRule not found: " + entity.getId());
        }

        if (rows > 1) {
            throw new IllegalStateException("More than one row affected");
        }

        return entity;
    }

    @Override
    public UrlRedirectRuleModel insert(UrlRedirectRuleModel entity) {
        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        UrlRedirectRulesRecord record = mapper.toRecord(entity);

        int rows = dsl.executeInsert(record);

        if (rows != 1) {
            throw new RuntimeException("Failed to insert url redirect rule");
        }

        return entity;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(URL_REDIRECT_RULES)
                .where(URL_REDIRECT_RULES.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<UrlRedirectRuleModel> findById(Long id) {
        return dsl.selectFrom(URL_REDIRECT_RULES)
                .where(URL_REDIRECT_RULES.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(URL_REDIRECT_RULES)
                        .where(URL_REDIRECT_RULES.ID.eq(id))
        );
    }
}