package com.read.api.repository;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.infrastructure.persistence.entity.UrlRedirectRuleEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UrlRedirectRuleRepositoryImplTest extends BaseRepositoryTest {

    @BeforeEach
    void setup() {
        template.dropCollection(
                UrlRedirectRuleEntity.class
        );
    }

    @Test
    void should_save_url_redirect_rule() {
        createUrlRedirectRule();
    }

    @Test
    void should_find_url_redirect_rule_by_id() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        var found =
                urlRedirectRuleRepository.findById(
                        saved.getId()
                );

        assertTrue(found.isPresent());

        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_verify_if_url_redirect_rule_exists() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        assertTrue(
                urlRedirectRuleRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_delete_url_redirect_rule() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        int deleted =
                urlRedirectRuleRepository.deleteById(
                        saved.getId()
                );

        assertEquals(1, deleted);

        assertFalse(
                urlRedirectRuleRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_find_by_url_id_filter() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setUrlId(
                saved.getUrlId()
        );

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_country_code_filter() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setCountryCode(
                saved.getCountryCode()
        );

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(
                page.getContent()
        ).isNotEmpty();
    }

    @Test
    void should_find_by_region_filter() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setRegion(
                saved.getRegion()
        );

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(
                page.getContent()
        ).isNotEmpty();
    }

    @Test
    void should_find_by_continent_filter() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setContinent(
                saved.getContinent()
        );

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_os_filter() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setOs(
                saved.getOs()
        );

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_browser_filter() {

        UrlRedirectRuleModel saved =
                createUrlRedirectRule();

        UrlRedirectRuleFilter filter =
                new UrlRedirectRuleFilter();

        filter.setBrowser(
                saved.getBrowser()
        );

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_match_type_filter() {
        UrlRedirectRuleModel saved = createUrlRedirectRule();

        UrlRedirectRuleFilter filter = new UrlRedirectRuleFilter();

        filter.setMatchType(saved.getMatchType());

        var page = urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_by_redirect_url_filter() {

        UrlRedirectRuleModel saved = createUrlRedirectRule();
        UrlRedirectRuleFilter filter = new UrlRedirectRuleFilter();

        filter.setRedirectUrl(saved.getRedirectUrl());

        var page = urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void should_find_by_rule_hash_filter() {

        UrlRedirectRuleModel saved = createUrlRedirectRule();

        UrlRedirectRuleFilter filter = new UrlRedirectRuleFilter();

        filter.setRuleHash(saved.getRuleHash());

        var page = urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void should_find_by_priority_filter() {

        UrlRedirectRuleModel saved = createUrlRedirectRule();

        UrlRedirectRuleFilter filter = new UrlRedirectRuleFilter();

        filter.setPriority(saved.getPriority());

        var page =
                urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_by_active_filter() {

        UrlRedirectRuleModel saved = createUrlRedirectRule();
        UrlRedirectRuleFilter filter = new UrlRedirectRuleFilter();

        filter.setActive(saved.isActive());

        var page = urlRedirectRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_active_rules_by_url_id_within_valid_date_range() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(this.generator.nextId());
        rule.setUrlId(555L);
        rule.setActive(true);
        rule.setStartAt(LocalDateTime.now().minusDays(1));
        rule.setEndAt(LocalDateTime.now().plusDays(1));

        urlRedirectRuleRepository.save(rule);

        List<UrlRedirectRuleModel> activeRules = urlRedirectRuleRepository.findActiveRulesByUrlId(555L);

        assertThat(activeRules).isNotEmpty();
        assertThat(activeRules.get(0).getUrlId()).isEqualTo(555L);
    }

    @Test
    void should_not_return_rule_when_it_is_inactive() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(this.generator.nextId());
        rule.setUrlId(777L);
        rule.setActive(false);
        rule.setStartAt(LocalDateTime.now().minusDays(1));
        rule.setEndAt(LocalDateTime.now().plusDays(1));

        urlRedirectRuleRepository.save(rule);

        List<UrlRedirectRuleModel> activeRules = urlRedirectRuleRepository.findActiveRulesByUrlId(777L);

        assertThat(activeRules).isEmpty();
    }

    @Test
    void should_not_return_rule_when_expired() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(this.generator.nextId());
        rule.setUrlId(888L);
        rule.setActive(true);
        rule.setStartAt(LocalDateTime.now().minusDays(5));
        rule.setEndAt(LocalDateTime.now().minusDays(1)); // Expirada há 1 dia

        urlRedirectRuleRepository.save(rule);

        List<UrlRedirectRuleModel> activeRules = urlRedirectRuleRepository.findActiveRulesByUrlId(888L);

        assertThat(activeRules).isEmpty();
    }

    @Test
    void should_find_active_rules_when_dates_are_null_or_missing() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setId(this.generator.nextId());
        rule.setUrlId(999L);
        rule.setActive(true);
        rule.setStartAt(null);
        rule.setEndAt(null);

        urlRedirectRuleRepository.save(rule);

        List<UrlRedirectRuleModel> activeRules = urlRedirectRuleRepository.findActiveRulesByUrlId(999L);

        assertThat(activeRules).isNotEmpty();
        assertThat(activeRules.getFirst().getUrlId()).isEqualTo(999L);
    }

    @Test
    void should_find_only_url_id_by_id_projection() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        Long ruleId = this.generator.nextId();
        rule.setId(ruleId);
        rule.setUrlId(12345L);
        rule.setActive(true);
        rule.setRedirectUrl("https://redirect-test.com");

        urlRedirectRuleRepository.save(rule);

        Optional<Long> projectedUrlId = urlRedirectRuleRepository.findUrlIdById(ruleId);

        assertTrue(projectedUrlId.isPresent());
        assertEquals(12345L, projectedUrlId.get());
    }

    @Test
    void should_return_empty_optional_when_finding_url_id_by_non_existent_id() {
        Optional<Long> projectedUrlId = urlRedirectRuleRepository.findUrlIdById(this.generator.nextId());

        assertFalse(projectedUrlId.isPresent());
    }
}