package com.read.api.repository;

import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.infrastructure.persistence.entity.UrlAccessRuleEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UrlAccessRuleRepositoryImplTest extends BaseRepositoryTest {

    @BeforeEach
    void setup() {
        template.dropCollection(UrlAccessRuleEntity.class);
    }

    @Test
    void should_save_url_access_rule() {
        createUrlAccessRule();
    }

    @Test
    void should_find_url_access_rule_by_id() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        var found =
                urlAccessRuleRepository.findById(
                        saved.getId()
                );

        assertTrue(found.isPresent());

        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_verify_if_url_access_rule_exists() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        assertTrue(
                urlAccessRuleRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_delete_url_access_rule() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        int deleted =
                urlAccessRuleRepository.deleteById(
                        saved.getId()
                );

        assertEquals(1, deleted);

        assertFalse(
                urlAccessRuleRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_find_url_access_rule_by_url_id_filter() {
        UrlAccessRuleModel saved = createUrlAccessRule();

        UrlAccessRuleFilter filter = new UrlAccessRuleFilter();

        filter.setUrlId(saved.getUrlId());

        var page = urlAccessRuleRepository.findAll(filter, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_url_access_rule_by_type_filter() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        UrlAccessRuleFilter filter =
                new UrlAccessRuleFilter();

        filter.setType(
                saved.getType()
        );

        var page =
                urlAccessRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_url_access_rule_by_rule_value_filter() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        UrlAccessRuleFilter filter =
                new UrlAccessRuleFilter();

        filter.setRuleValue(
                saved.getRuleValue()
        );

        var page =
                urlAccessRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(
                page.getContent()
        ).isNotEmpty();
    }

    @Test
    void should_find_url_access_rule_by_active_filter() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        UrlAccessRuleFilter filter =
                new UrlAccessRuleFilter();

        filter.setActive(
                saved.isActive()
        );

        var page =
                urlAccessRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_url_access_rule_by_assigned_by_user_id_filter() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        UrlAccessRuleFilter filter =
                new UrlAccessRuleFilter();

        filter.setAssignedByUserId(
                saved.getAssignedByUserId()
        );

        var page =
                urlAccessRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_find_url_access_rule_by_expiration_range() {

        UrlAccessRuleModel saved =
                createUrlAccessRule();

        UrlAccessRuleFilter filter =
                new UrlAccessRuleFilter();

        filter.setExpiresAtAfter(
                saved.getExpiresAt().minusDays(1)
        );

        filter.setExpiresAtBefore(
                saved.getExpiresAt().plusDays(1)
        );

        var page =
                urlAccessRuleRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }
}