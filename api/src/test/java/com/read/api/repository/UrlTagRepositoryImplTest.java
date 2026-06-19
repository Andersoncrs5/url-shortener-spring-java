package com.read.api.repository;

import com.read.api.domain.model.UrlTagModel;
import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.infrastructure.persistence.entity.UrlTagEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UrlTagRepositoryImplTest extends BaseRepositoryTest {

    @Autowired
    private UrlTagRepository urlTagRepository;

    @BeforeEach
    void setup() {
        template.dropCollection(UrlTagEntity.class);
    }

    @Test
    void should_save_url_tag() {
        UrlTagModel saved = createUrlTag();
        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    void should_find_url_tag_by_id() {
        UrlTagModel saved = createUrlTag();

        Optional<UrlTagModel> found = urlTagRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(saved.getName(), found.get().getName());
    }

    @Test
    void should_verify_if_url_tag_exists_by_name() {
        UrlTagModel saved = createUrlTag();

        boolean exists = urlTagRepository.existsByName(saved.getName());

        assertTrue(exists);
    }

    @Test
    void should_verify_if_url_tag_exists_by_slug() {
        UrlTagModel saved = createUrlTag();

        boolean exists = urlTagRepository.existsBySlug(saved.getSlug());

        assertTrue(exists);
    }

    @Test
    void should_find_url_tags_by_user_id_filter() {
        UrlTagModel saved = createUrlTag();

        UrlTagFilter filter = new UrlTagFilter();
        filter.setUserId(saved.getUserId());

        var page = urlTagRepository.findAll(filter, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_url_tags_by_name_filter() {
        UrlTagModel saved = createUrlTag();

        UrlTagFilter filter = new UrlTagFilter();
        filter.setName(saved.getName().substring(0, 3)); // Testa busca parcial (Like)

        var page = urlTagRepository.findAll(filter, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void should_find_url_tags_by_slug_filter() {
        UrlTagModel saved = createUrlTag();

        UrlTagFilter filter = new UrlTagFilter();
        filter.setSlug(saved.getSlug());

        var page = urlTagRepository.findAll(filter, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_url_tags_by_active_filter() {
        UrlTagModel saved = createUrlTag();

        UrlTagFilter filter = new UrlTagFilter();
        filter.setActive(saved.isActive());

        var page = urlTagRepository.findAll(filter, PageRequest.of(0, 10));

        assertThat(page.getContent()).isNotEmpty();
    }
}