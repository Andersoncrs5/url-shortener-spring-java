package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.BaseRepositoryTest;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.shared.utils.Base62;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JooqUrlRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JooqUrlRepository repository;

    private UserModel user;

    @BeforeEach
    void setup() {
        user = help.createUser();
    }

    @Test
    void shouldInsertUrl() {
        UrlModel url = new UrlModel();

        url.setUserId(user.getId());
        url.setShortCode(Base62.encode(this.generator.nextId()));
        url.setDescription("My description");
        url.setFaviconUrl("https://site.com/favicon.ico");
        url.setOriginalUrl("https://example.com/page");
        url.setTitle("Example");
        url.setDomain("example.com");
        url.setStatus(com.write.api.core.domain.enums.UrlStatusEnum.ACTIVE);
        url.setAccessType(com.write.api.core.domain.enums.UrlAccessTypeEnum.PUBLIC);
        url.setPasswordHash("secret");
        url.setCustomAlias(true);
        url.setExpiresAt(LocalDateTime.now().plusDays(1));

        UrlModel saved = repository.insert(url);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getShortCode()).isEqualTo(url.getShortCode());
        assertThat(saved.getDescription()).isEqualTo("My description");
        assertThat(saved.getFaviconUrl()).isEqualTo("https://site.com/favicon.ico");
        assertThat(saved.getOriginalUrl()).isEqualTo("https://example.com/page");
        assertThat(saved.getTitle()).isEqualTo("Example");
        assertThat(saved.getDomain()).isEqualTo("example.com");
        assertThat(saved.getStatus()).isEqualTo(com.write.api.core.domain.enums.UrlStatusEnum.ACTIVE);
        assertThat(saved.getAccessType()).isEqualTo(com.write.api.core.domain.enums.UrlAccessTypeEnum.PUBLIC);
        assertThat(saved.getPasswordHash()).isEqualTo("secret");
        assertThat(saved.isCustomAlias()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUrl() {
        UrlModel saved = help.createUrl(user);
        var key = UUID.randomUUID().toString();

        saved.setTitle("Updated title " + key);
        saved.setDescription("Updated description " + key);
        saved.setShortCode("short-" + key.substring(0, 8));
        saved.setDomain("newdomain.com");
        saved.setCustomAlias(false);

        UrlModel updated = repository.save(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getTitle()).isEqualTo("Updated title " + key);
        assertThat(updated.getDescription()).isEqualTo("Updated description " + key);
        assertThat(updated.getShortCode()).isEqualTo("short-" + key.substring(0, 8));
        assertThat(updated.getDomain()).isEqualTo("newdomain.com");
        assertThat(updated.isCustomAlias()).isFalse();
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteUrlById() {
        UrlModel saved = help.createUrl(user);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);

        Optional<UrlModel> result = repository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindUrlById() {
        UrlModel saved = help.createUrl(user);

        Optional<UrlModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();

        UrlModel url = result.get();

        assertThat(url.getId()).isEqualTo(saved.getId());
        assertThat(url.getUserId()).isEqualTo(user.getId());
        assertThat(url.getShortCode()).isEqualTo(saved.getShortCode());
        assertThat(url.getOriginalUrl()).isEqualTo(saved.getOriginalUrl());
        assertThat(url.getTitle()).isEqualTo(saved.getTitle());
    }

    @Test
    void shouldReturnEmptyWhenUrlNotFound() {
        Optional<UrlModel> result = repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenUrlExistsById() {
        UrlModel saved = help.createUrl(user);

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUrlDoesNotExistById() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenShortCodeExists() {
        UrlModel saved = help.createUrl(user);

        boolean exists = repository.existsByShortCode(saved.getShortCode());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenShortCodeDoesNotExist() {
        boolean exists = repository.existsByShortCode("short-" + UUID.randomUUID());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldNotAllowDuplicateShortCode() {
        UrlModel first = help.createUrl(user);

        UrlModel second = new UrlModel();
        second.setUserId(user.getId());
        second.setShortCode(first.getShortCode());
        second.setOriginalUrl("https://another.com");
        second.setStatus(com.write.api.core.domain.enums.UrlStatusEnum.ACTIVE);
        second.setAccessType(com.write.api.core.domain.enums.UrlAccessTypeEnum.PUBLIC);

        assertThatThrownBy(() -> repository.insert(second))
                .isInstanceOf(Exception.class);
    }
}