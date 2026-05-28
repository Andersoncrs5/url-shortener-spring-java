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
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.URL_REDIRECT_RULES;

@Repository
@RequiredArgsConstructor
public class JooqUrlRedirectRuleRepository implements IUrlRedirectRuleRepository {

    private final DSLContext dsl;
    private final SnowflakeIdGenerator idGen;
    private final UrlRedirectRuleRepositoryMapper mapper;

    @Override
    public UrlRedirectRuleModel save(UrlRedirectRuleModel entity) {

        int rows = dsl.update(URL_REDIRECT_RULES)
                .set(URL_REDIRECT_RULES.URL_ID, entity.getUrlId())
                .set(URL_REDIRECT_RULES.COUNTRY_CODE, entity.getCountryCode())
                .set(URL_REDIRECT_RULES.REGION, entity.getRegion())
                .set(URL_REDIRECT_RULES.CONTINENT,
                        entity.getContinent() != null
                                ? UrlRedirectRulesContinent.valueOf(entity.getContinent().name())
                                : null)
                .set(URL_REDIRECT_RULES.OS,
                        entity.getOs() != null
                                ? UrlRedirectRulesOs.valueOf(entity.getOs().name())
                                : null)
                .set(URL_REDIRECT_RULES.BROWSER,
                        entity.getBrowser() != null
                                ? UrlRedirectRulesBrowser.valueOf(entity.getBrowser().name())
                                : null)
                .set(URL_REDIRECT_RULES.MATCH_TYPE,
                        entity.getMatchType() != null
                                ? UrlRedirectRulesMatchType.valueOf(entity.getMatchType().name())
                                : null)
                .set(URL_REDIRECT_RULES.REDIRECT_URL, entity.getRedirectUrl())
                .set(URL_REDIRECT_RULES.RULE_HASH, entity.getRuleHash())
                .set(URL_REDIRECT_RULES.PRIORITY, entity.getPriority())
                .set(URL_REDIRECT_RULES.ACTIVE, entity.isActive())
                .set(URL_REDIRECT_RULES.START_AT, entity.getStartAt())
                .set(URL_REDIRECT_RULES.END_AT, entity.getEndAt())
                .set(URL_REDIRECT_RULES.UPDATED_AT, LocalDateTime.now())
                .where(URL_REDIRECT_RULES.ID.eq(entity.getId()))
                .execute();

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

        int rows = dsl.insertInto(URL_REDIRECT_RULES)
                .set(URL_REDIRECT_RULES.ID, id)
                .set(URL_REDIRECT_RULES.URL_ID, entity.getUrlId())
                .set(URL_REDIRECT_RULES.COUNTRY_CODE, entity.getCountryCode())
                .set(URL_REDIRECT_RULES.REGION, entity.getRegion())
                .set(URL_REDIRECT_RULES.CONTINENT,
                        entity.getContinent() != null
                                ? UrlRedirectRulesContinent.valueOf(entity.getContinent().name())
                                : null)
                .set(URL_REDIRECT_RULES.OS,
                        entity.getOs() != null
                                ? UrlRedirectRulesOs.valueOf(entity.getOs().name())
                                : null)
                .set(URL_REDIRECT_RULES.BROWSER,
                        entity.getBrowser() != null
                                ? UrlRedirectRulesBrowser.valueOf(entity.getBrowser().name())
                                : null)
                .set(URL_REDIRECT_RULES.MATCH_TYPE,
                        entity.getMatchType() != null
                                ? UrlRedirectRulesMatchType.valueOf(entity.getMatchType().name())
                                : null)
                .set(URL_REDIRECT_RULES.REDIRECT_URL, entity.getRedirectUrl())
                .set(URL_REDIRECT_RULES.RULE_HASH, entity.getRuleHash())
                .set(URL_REDIRECT_RULES.PRIORITY, entity.getPriority())
                .set(URL_REDIRECT_RULES.ACTIVE, entity.isActive())
                .set(URL_REDIRECT_RULES.START_AT, entity.getStartAt())
                .set(URL_REDIRECT_RULES.END_AT, entity.getEndAt())
                .set(URL_REDIRECT_RULES.CREATED_AT, now)
                .set(URL_REDIRECT_RULES.UPDATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException("Failed to insert url redirect rule");
        }

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

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