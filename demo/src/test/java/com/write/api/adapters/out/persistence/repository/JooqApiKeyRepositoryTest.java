package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.model.ApiKeyModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.Tables;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
class JooqApiKeyRepositoryTest {

    @Autowired
    private HelpRepositoryTest help;

    @Autowired
    private JooqApiKeyRepository repository;

    @Autowired
    private SnowflakeIdGenerator generator;

    @Autowired
    private DSLContext dsl;

    private UserModel user;

    @BeforeEach
    void setup() {
        dsl.deleteFrom(Tables.API_KEYS).execute();

        user = help.createUser();
    }

    @Test
    void shouldInsertApiKey() {
        ApiKeyModel apiKey = buildApiKey();

        ApiKeyModel saved = repository.insert(apiKey);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getName()).isEqualTo("Production");
        assertThat(saved.getKeyHash()).isEqualTo("sha256-test-key");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateApiKey() {
        ApiKeyModel saved = repository.insert(buildApiKey());

        saved.setName("CI/CD");
        saved.setActive(false);

        ApiKeyModel updated = repository.save(saved);

        assertThat(updated.getName()).isEqualTo("CI/CD");
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
        assertThat(found.getName()).isEqualTo("Production");
        assertThat(found.getKeyHash()).isEqualTo("sha256-test-key");
    }

    @Test
    void shouldReturnEmptyWhenFindByIdNotFound() {
        Optional<ApiKeyModel> result =
                repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindByKeyHash() {
        repository.insert(buildApiKey());

        Optional<ApiKeyModel> result =
                repository.findByKeyHash("sha256-test-key");

        assertThat(result).isPresent();
        assertThat(result.get().getName())
                .isEqualTo("Production");
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

    @Test
    void shouldFailWhenInsertingDuplicateHash() {
        repository.insert(buildApiKey());

        ApiKeyModel duplicate = buildApiKey();
        duplicate.setName("Another");

        assertThatThrownBy(
                () -> repository.insert(duplicate)
        )
                .isInstanceOf(Exception.class);
    }

    private ApiKeyModel buildApiKey() {

        ApiKeyModel apiKey = new ApiKeyModel();

        apiKey.setUserId(user.getId());
        apiKey.setName("Production");
        apiKey.setKeyHash("sha256-test-key");
        apiKey.setActive(true);
        apiKey.setLastUsedAt(
                LocalDateTime.now().minusHours(1)
        );
        apiKey.setExpiresAt(
                LocalDateTime.now().plusDays(30)
        );

        return apiKey;
    }
}