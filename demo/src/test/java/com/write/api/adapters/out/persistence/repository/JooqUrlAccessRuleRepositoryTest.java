package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.core.domain.model.UrlModel;
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
class JooqUrlAccessRuleRepositoryTest {

    @Autowired
    private HelpRepositoryTest help;

    @Autowired
    private JooqUrlAccessRuleRepository repository;

    @Autowired
    private SnowflakeIdGenerator generator;

    @Autowired
    private DSLContext dsl;

    private UserModel user;
    private UrlModel url;

    @BeforeEach
    void setup() {
        dsl.deleteFrom(Tables.URL_ACCESS_RULE).execute();
        user = help.createUser();
        url = help.createUrl(user);
    }

    @Test
    void shouldInsertUrlAccessRule() {
        UrlAccessRuleModel rule = buildRule();

        UrlAccessRuleModel saved = repository.insert(rule);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUrlId()).isEqualTo(url.getId());
        assertThat(saved.getType()).isEqualTo(UrlAccessRuleTypeEnum.COUNTRY_BLOCK);
        assertThat(saved.getRuleValue()).isEqualTo("BR");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getAssignedByUserId()).isEqualTo(user.getId());
        assertThat(saved.getExpiresAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUrlAccessRule() {
        UrlAccessRuleModel saved = repository.insert(buildRule());

        saved.setRuleValue("US");
        saved.setActive(false);
        saved.setType(UrlAccessRuleTypeEnum.COUNTRY_ALLOW);
        saved.setExpiresAt(LocalDateTime.now().plusDays(10));

        UrlAccessRuleModel updated = repository.save(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getUrlId()).isEqualTo(url.getId());
        assertThat(updated.getType()).isEqualTo(UrlAccessRuleTypeEnum.COUNTRY_ALLOW);
        assertThat(updated.getRuleValue()).isEqualTo("US");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getAssignedByUserId()).isEqualTo(user.getId());
        assertThat(updated.getExpiresAt()).isNotNull();
    }

    @Test
    void shouldFindUrlAccessRuleById() {
        UrlAccessRuleModel saved = repository.insert(buildRule());

        Optional<UrlAccessRuleModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();

        UrlAccessRuleModel found = result.get();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUrlId()).isEqualTo(url.getId());
        assertThat(found.getType()).isEqualTo(UrlAccessRuleTypeEnum.COUNTRY_BLOCK);
        assertThat(found.getRuleValue()).isEqualTo("BR");
        assertThat(found.isActive()).isTrue();
        assertThat(found.getAssignedByUserId()).isEqualTo(user.getId());
        assertThat(found.getExpiresAt()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenUrlAccessRuleNotFound() {
        Optional<UrlAccessRuleModel> result = repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteUrlAccessRuleById() {
        UrlAccessRuleModel saved = repository.insert(buildRule());

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
    }

    @Test
    void shouldReturnTrueWhenUrlAccessRuleExistsById() {
        UrlAccessRuleModel saved = repository.insert(buildRule());

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUrlAccessRuleDoesNotExistById() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteAndThenNotFindUrlAccessRule() {
        UrlAccessRuleModel saved = repository.insert(buildRule());

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
        assertThat(repository.findById(saved.getId())).isEmpty();
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void shouldReturnZeroWhenDeletingNonExistingUrlAccessRule() {
        int rows = repository.deleteById(generator.nextId());

        assertThat(rows).isZero();
    }

    @Test
    void shouldFailWhenUpdatingNonExistingUrlAccessRule() {
        UrlAccessRuleModel rule = buildRule();
        rule.setId(generator.nextId());

        assertThatThrownBy(() -> repository.save(rule))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("UrlAccessRule not found");
    }

    @Test
    void shouldFailWhenInsertingDuplicateRuleForSameUrlTypeAndValue() {
        repository.insert(buildRule());

        UrlAccessRuleModel duplicate = buildRule();

        assertThatThrownBy(() -> repository.insert(duplicate))
                .isInstanceOf(Exception.class);
    }

    private UrlAccessRuleModel buildRule() {
        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setUrlId(url.getId());
        rule.setType(UrlAccessRuleTypeEnum.COUNTRY_BLOCK);
        rule.setRuleValue("BR");
        rule.setActive(true);
        rule.setAssignedByUserId(user.getId());
        rule.setExpiresAt(LocalDateTime.now().plusDays(1));
        rule.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        rule.setUpdatedAt(LocalDateTime.now().minusMinutes(1));
        return rule;
    }
}