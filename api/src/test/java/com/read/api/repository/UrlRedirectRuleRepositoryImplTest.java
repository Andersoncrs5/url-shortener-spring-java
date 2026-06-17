package com.read.api.repository;

import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.infrastructure.persistence.entity.UrlRedirectRuleEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

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
}