package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.BaseRepositoryTest;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JooqApiKeyRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JooqApiKeyRepository repository;

    private UserModel user;
    private UserModel owner;

    @BeforeEach
    void setup() {
        user = help.createUser();
        owner = help.createUser();
    }

    @Test
    void shouldInsertApiKey() {
        ApiKeyModel apiKey = buildApiKey();

        ApiKeyModel saved = repository.insert(apiKey);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getName()).containsIgnoringCase("Production");
        assertThat(saved.getKeyHash()).containsIgnoringCase("sha256-test-key");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateApiKey() {
        ApiKeyModel saved = repository.insert(buildApiKey());

        saved.setName("CI/CD" + this.generator.nextId());
        saved.setActive(false);

        ApiKeyModel updated = repository.save(saved);

        assertThat(updated.getName()).containsIgnoringCase(saved.getName());
        assertThat(updated.isActive()).isFalse();
    }

    @Test
    void shouldFindById() {
        ApiKeyModel saved = repository.insert(buildApiKey());

        Optional<ApiKeyModel> result =
                repository.findById(saved.getId());

        assertThat(result).isPresent();

        ApiKeyModel found = result.get();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUserId()).isEqualTo(user.getId());
        assertThat(found.getName()).containsIgnoringCase("Production");
        assertThat(found.getKeyHash()).containsIgnoringCase("sha256-test-key");
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        Optional<ApiKeyModel> result =
                repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindByKeyHash() {
        ApiKeyModel insert = repository.insert(buildApiKey());

        Optional<ApiKeyModel> result =
                repository.findByKeyHash(insert.getKeyHash());

        assertThat(result).isPresent();
        assertThat(result.get().getName())
                .containsIgnoringCase("Production");
    }

    @Test
    void shouldReturnEmptyWhenKeyHashNotFound() {
        Optional<ApiKeyModel> result =
                repository.findByKeyHash("not-found");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteApiKey() {
        ApiKeyModel saved = repository.insert(buildApiKey());

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
    }

    @Test
    void shouldReturnZeroWhenDeletingNonExistingApiKey() {
        int rows =
                repository.deleteById(generator.nextId());

        assertThat(rows).isZero();
    }

    @Test
    void shouldReturnTrueWhenApiKeyExists() {
        ApiKeyModel saved = repository.insert(buildApiKey());

        assertThat(
                repository.existsById(saved.getId())
        ).isTrue();
    }

    @Test
    void shouldReturnFalseWhenApiKeyDoesNotExist() {
        assertThat(
                repository.existsById(generator.nextId())
        ).isFalse();
    }

    @Test
    void shouldDeleteAndThenNotFindApiKey() {
        ApiKeyModel saved = repository.insert(buildApiKey());

        repository.deleteById(saved.getId());

        assertThat(
                repository.findById(saved.getId())
        ).isEmpty();

        assertThat(
                repository.existsById(saved.getId())
        ).isFalse();
    }

    @Test
    void shouldFailWhenUpdatingNonExistingApiKey() {
        ApiKeyModel apiKey = buildApiKey();
        apiKey.setId(generator.nextId());

        assertThatThrownBy(
                () -> repository.save(apiKey)
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ApiKey not found");
    }

    private ApiKeyModel buildApiKey() {

        ApiKeyModel apiKey = new ApiKeyModel();

        apiKey.setUserId(user.getId());
        apiKey.setName("Production" + this.generator.nextId());
        apiKey.setKeyHash("sha256-test-key"  + this.generator.nextId());
        apiKey.setActive(true);
        apiKey.setLastUsedAt(
                LocalDateTime.now().minusHours(1)
        );
        apiKey.setExpiresAt(
                LocalDateTime.now().plusDays(30)
        );
        apiKey.setOwnerUserId(owner.getId());

        return apiKey;
    }
}