package com.read.api.repository;

import com.read.api.api.dto.url.UrlFilter;
import com.read.api.domain.model.UrlModel;
import com.read.api.infrastructure.persistence.entity.UrlEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UrlRepositoryImplTest extends BaseRepositoryTest {

    @BeforeEach
    void setup() {
        template.dropCollection(UrlEntity.class);
    }

    @Test
    void should_save_url() {
        createUrl();
    }

    @Test
    void should_insert_url() {

        UrlModel model = createUrl();

        assertNotNull(model.getId());
    }

    @Test
    void should_find_by_id() {

        UrlModel saved = createUrl();

        var found =
                urlRepository.findById(saved.getId());

        assertTrue(found.isPresent());

        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_exists_by_id() {

        UrlModel saved = createUrl();

        assertTrue(
                urlRepository.existsById(saved.getId())
        );
    }

    @Test
    void should_delete_by_id() {

        UrlModel saved = createUrl();

        int deleted =
                urlRepository.deleteById(saved.getId());

        assertEquals(1, deleted);

        assertFalse(
                urlRepository.existsById(saved.getId())
        );
    }

    @Test
    void should_find_by_short_code() {

        UrlModel saved = createUrl();

        var found =
                urlRepository.findByShortCode(
                        saved.getShortCode()
                );

        assertTrue(found.isPresent());

        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_filter_by_user_id() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setUserId(saved.getUserId());

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_filter_by_short_code() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setShortCode(saved.getShortCode());

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .isNotEmpty();
    }

    @Test
    void should_filter_by_title() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setTitle(saved.getTitle());

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .isNotEmpty();
    }

    @Test
    void should_filter_by_domain() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setDomain(saved.getDomain());

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .isNotEmpty();
    }

    @Test
    void should_filter_by_status() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setStatus(saved.getStatus());

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_filter_by_access_type() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setAccessType(
                saved.getAccessType()
        );

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_filter_by_custom_alias() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setCustomAlias(
                saved.isCustomAlias()
        );

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_filter_by_single_tag() {
        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        addTagInUrl(saved, List.of("java"));

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(1);
    }

    @Test
    void should_filter_by_any_tag() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        addTagInUrl(saved, List.of("java", "spring"));

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .isNotEmpty();
    }

    @Test
    void should_filter_by_all_tags() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setMatchAllTags(true);
        addTagInUrl(saved, List.of("java", "spring"));

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .hasSize(1);
    }

    @Test
    void should_filter_by_expires_at_range() {

        UrlModel saved = createUrl();

        UrlFilter filter = new UrlFilter();

        filter.setExpiresAtMin(
                saved.getExpiresAt().minusDays(1)
        );

        filter.setExpiresAtMax(
                saved.getExpiresAt().plusDays(1)
        );

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertEquals(
                1,
                page.getTotalElements()
        );
    }

    @Test
    void should_return_empty_when_tag_not_exists() {

        createUrl();

        UrlFilter filter = new UrlFilter();

        filter.getTags().add("non-existent");

        var page =
                urlRepository.findAll(
                        filter,
                        PageRequest.of(0, 10)
                );

        assertThat(page.getContent())
                .isEmpty();
    }
}