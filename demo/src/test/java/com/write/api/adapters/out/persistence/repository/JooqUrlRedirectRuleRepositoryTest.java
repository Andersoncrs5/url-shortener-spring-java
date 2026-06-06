package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.BaseRepositoryTest;
import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
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
class JooqUrlRedirectRuleRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JooqUrlRedirectRuleRepository repository;

    private UserModel user;
    private UrlModel url;

    @BeforeEach
    void setup() {
        user = help.createUser();
        url = help.createUrl(user);
    }

    @Test
    void shouldInsertUrlRedirectRule() {
        UrlRedirectRuleModel rule = buildRule();

        UrlRedirectRuleModel saved = repository.insert(rule);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUrlId()).isEqualTo(url.getId());
        assertThat(saved.getCountryCode()).isEqualTo("BR");
        assertThat(saved.getRegion()).isEqualTo("PI");
        assertThat(saved.getContinent()).isEqualTo(ContinentEnum.SOUTH_AMERICA);
        assertThat(saved.getOs()).isEqualTo(OperatingSystemEnum.ANDROID);
        assertThat(saved.getBrowser()).isEqualTo(BrowserEnum.CHROME);
        assertThat(saved.getMatchType()).isEqualTo(MatchTypeEnum.EXACT);
        assertThat(saved.getRedirectUrl()).isEqualTo("https://example.com/br");
        assertThat(saved.getRuleHash()).isNotBlank();
        assertThat(saved.getPriority()).isEqualTo(1);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUrlRedirectRule() {
        UrlRedirectRuleModel saved = help.createUrlRedirectRule(url);

        saved.setRedirectUrl("https://example.com/br-new");
        saved.setPriority(10);
        saved.setActive(false);
        saved.setOs(OperatingSystemEnum.IOS);
        saved.setBrowser(BrowserEnum.SAFARI);

        UrlRedirectRuleModel updated = repository.save(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getRedirectUrl()).isEqualTo("https://example.com/br-new");
        assertThat(updated.getPriority()).isEqualTo(10);
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getOs()).isEqualTo(OperatingSystemEnum.IOS);
        assertThat(updated.getBrowser()).isEqualTo(BrowserEnum.SAFARI);
    }

    @Test
    void shouldFindUrlRedirectRuleById() {
        UrlRedirectRuleModel saved = help.createUrlRedirectRule(url);

        Optional<UrlRedirectRuleModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();

        UrlRedirectRuleModel found = result.get();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUrlId()).isEqualTo(url.getId());
        assertThat(found.getCountryCode()).isEqualTo("BR");
        assertThat(found.getRegion()).isEqualTo("PI");
        assertThat(found.getContinent()).isEqualTo(ContinentEnum.SOUTH_AMERICA);
        assertThat(found.getOs()).isEqualTo(OperatingSystemEnum.ANDROID);
        assertThat(found.getBrowser()).isEqualTo(BrowserEnum.CHROME);
        assertThat(found.getMatchType()).isEqualTo(MatchTypeEnum.EXACT);
        assertThat(found.getRedirectUrl()).isEqualTo("https://example.com/br");
        assertThat(found.getRuleHash()).isNotBlank();
    }

    @Test
    void shouldReturnEmptyWhenUrlRedirectRuleNotFound() {
        Optional<UrlRedirectRuleModel> result = repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteUrlRedirectRuleById() {
        UrlRedirectRuleModel saved = help.createUrlRedirectRule(url);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
    }

    @Test
    void shouldReturnTrueWhenUrlRedirectRuleExistsById() {
        UrlRedirectRuleModel saved = help.createUrlRedirectRule(url);

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUrlRedirectRuleDoesNotExistById() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldDeleteAndThenNotFindUrlRedirectRule() {
        UrlRedirectRuleModel saved = help.createUrlRedirectRule(url);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
        assertThat(repository.findById(saved.getId())).isEmpty();
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void shouldReturnZeroWhenDeletingNonExistingUrlRedirectRule() {
        int rows = repository.deleteById(generator.nextId());

        assertThat(rows).isZero();
    }

    @Test
    void shouldFailWhenUpdatingNonExistingUrlRedirectRule() {
        UrlRedirectRuleModel rule = buildRule();
        rule.setId(generator.nextId());

        assertThatThrownBy(() -> repository.save(rule))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("UrlRedirectRule not found");
    }

    private UrlRedirectRuleModel buildRule() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();

        rule.setUrlId(url.getId());
        rule.setCountryCode("BR");
        rule.setRegion("PI");
        rule.setContinent(ContinentEnum.SOUTH_AMERICA);
        rule.setOs(OperatingSystemEnum.ANDROID);
        rule.setBrowser(BrowserEnum.CHROME);
        rule.setMatchType(MatchTypeEnum.EXACT);
        rule.setRedirectUrl("https://example.com/br");
        rule.setRuleHash("a".repeat(32) + this.generator.nextId());
        rule.setPriority(1);
        rule.setActive(true);
        rule.setStartAt(LocalDateTime.now().minusDays(1));
        rule.setEndAt(LocalDateTime.now().plusDays(1));

        return rule;
    }
}