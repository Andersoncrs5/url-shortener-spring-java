package com.write.api.adapters.out.persistence.help;

import com.write.api.adapters.out.persistence.repository.*;
import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.core.domain.model.*;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.shared.utils.Base62;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HelpRepositoryTest {

    SnowflakeIdGenerator generator;
    JooqUserRepository repository;
    JooqUrlTagRepository tagRepository;
    JooqUrlRepository urlRepository;
    JooqUrlTagLinkRepository urlTagLinkRepository;
    JooqUrlRedirectRuleRepository urlRedirectRuleRepository;
    JooqRoleRepository jooqRoleRepository;
    JooqUserRoleRepository userRoleRepository;
    JooqApiKeyRepository apiKeyRepository;

    public ApiKeyModel createApiKey(UserModel user) {
        ApiKeyModel apiKey = new ApiKeyModel();

        apiKey.setUserId(user.getId());
        apiKey.setName("Production");
        apiKey.setKeyHash("sha256-test-key");
        apiKey.setActive(true);
        apiKey.setLastUsedAt(LocalDateTime.now().minusHours(1));
        apiKey.setExpiresAt(LocalDateTime.now().plusDays(30));

        return apiKeyRepository.insert(apiKey);
    }

    public UserModel createUser() {
        UserModel user = new UserModel();
        user.setName("John Doe" + generator.nextId());
        user.setEmail("john" + generator.nextId() + "@example.com");
        user.setPasswordHash("54356435645625467245425");
        user.setActive(true);

        return repository.insert(user);
    }

    public UrlTagModel createUrlTag(UserModel user) {
        UrlTagModel tag = new UrlTagModel();
        var key = generator.nextId();

        tag.setUserId(user.getId());
        tag.setName("Backend" + key);
        tag.setSlug("backend" + key);
        tag.setColor("#000000");
        tag.setDescription("Backend tag");
        tag.setActive(true);

        UrlTagModel saved = tagRepository.insert(tag);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getName()).isEqualTo("Backend" + key);
        assertThat(saved.getSlug()).isEqualTo("backend" + key);
        assertThat(saved.getColor()).isEqualTo("#000000");
        assertThat(saved.getDescription()).isEqualTo("Backend tag");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        return saved;
    }

    public UrlModel createUrl(UserModel user) {
        UrlModel url = new UrlModel();
        var key = String.valueOf(generator.nextId());

        url.setUserId(user.getId());
        url.setShortCode(key);
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

        UrlModel saved = urlRepository.insert(url);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isEqualTo(1L);
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getShortCode()).isEqualTo(key);
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

        return saved;
    }

    public UrlTagLinkModel createTagToUrl(
            UserModel user,
            UrlModel url,
            UrlTagModel tag
    ) {
        UrlTagLinkModel link = new UrlTagLinkModel();

        link.setUrlId(url.getId());
        link.setTagId(tag.getId());
        link.setSortOrder((short) 1);
        link.setNote("Any note");
        link.setPrimaryTag(true);
        link.setCreatedBy(user.getId());

        UrlTagLinkModel saved = urlTagLinkRepository.insert(link);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUrlId()).isEqualTo(url.getId());
        assertThat(saved.getTagId()).isEqualTo(tag.getId());
        assertThat(saved.getSortOrder()).isEqualTo((short) 1);
        assertThat(saved.getNote()).isEqualTo("Any note");
        assertThat(saved.isPrimaryTag()).isTrue();
        assertThat(saved.getCreatedBy()).isEqualTo(user.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        return saved;
    }

    public UrlRedirectRuleModel createUrlRedirectRule(
            UrlModel url
    ) {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();

        rule.setUrlId(url.getId());
        rule.setId(generator.nextId());
        rule.setCountryCode("BR");
        rule.setRegion("PI");
        rule.setContinent(ContinentEnum.SOUTH_AMERICA);
        rule.setOs(OperatingSystemEnum.ANDROID);
        rule.setBrowser(BrowserEnum.CHROME);
        rule.setMatchType(MatchTypeEnum.EXACT);
        rule.setRedirectUrl("https://example.com/br");
        rule.setRuleHash("a".repeat(64));
        rule.setPriority(1);
        rule.setActive(true);
        rule.setStartAt(LocalDateTime.now().minusDays(1));
        rule.setEndAt(LocalDateTime.now().plusDays(1));

        return urlRedirectRuleRepository.insert(rule);
    }

    public RoleModel createRole() {
        long id = generator.nextId();
        String name = "ROLE_" + Base62.encode(id);

        RoleModel role = new RoleModel();
        role.setName(name);
        role.setDescription("System administrator");
        role.setActive(true);

        RoleModel saved = jooqRoleRepository.insert(role);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(saved.getDescription()).isEqualTo("System administrator");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        return saved;
    }

    public String generateRandomChars(int size) {
        StringBuilder builder = new StringBuilder();

        while (builder.length() < size) {
            builder.append(
                    Base62.encode(generator.nextId())
            );
        }

        return builder.substring(0, size);
    }

    public UserRoleModel createUserRole(
            UserModel user,
            RoleModel role,
            UserModel assignedBy
    ) {
        UserRoleModel model = new UserRoleModel();

        model.setUserId(user.getId());
        model.setRoleId(role.getId());
        model.setAssignedByUserId(assignedBy.getId());

        return userRoleRepository.insert(model);
    }
}