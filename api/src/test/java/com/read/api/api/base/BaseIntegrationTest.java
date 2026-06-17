package com.read.api.api.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.TestcontainersConfiguration;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.enums.UrlAccessTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UrlAccessRuleRepository;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.repository.UserRepository;
import com.read.api.domain.utils.Base62;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import com.read.api.infrastructure.config.security.TokenService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PROTECTED)
@Import(TestcontainersConfiguration.class)
public class BaseIntegrationTest {

    @Autowired SnowflakeIdGenerator generator;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired TokenService tokenService;

    @Autowired UserRepository userRepository;
    @Autowired UrlAccessRuleRepository urlAccessRuleRepository;
    @Autowired UrlRedirectRuleRepository urlRedirectRuleRepository;
    @Autowired UrlRepository urlRepository;

    protected UrlRedirectRuleModel createUrlRedirectRule() {
        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();

        LocalDateTime now = LocalDateTime.now();

        rule.setId(generator.nextId());
        rule.setUrlId(generator.nextId());
        rule.setCountryCode("BR");
        rule.setRegion("PI");
        rule.setRedirectUrl("https://google.com");
        rule.setPriority(10);
        rule.setActive(true);
        rule.setStartAt(now.minusDays(1));
        rule.setEndAt(now.plusDays(30));
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);

        return urlRedirectRuleRepository.insert(rule);
    }

    protected ArrayList<UrlRedirectRuleModel> createManyUrlRedirectRule(int amount) {

        ArrayList<UrlRedirectRuleModel> list =
                new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            list.add(createUrlRedirectRule());
        }

        return list;
    }

    protected String createUser() {

        UserModel user = new UserModel();

        user.setId(generator.nextId());
        user.setName("Pochita "+ this.generateRandomChars(50).toLowerCase() );
        user.setEmail("pochita" + this.generateRandomChars(51).toLowerCase() + "@gmail.com");
        user.setActive(true);
        user.setVersion(1L);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        UserModel inserted = userRepository.insert(user);

        return tokenService.generateToken(inserted);
    }

    protected UserModel createUser(
            String name,
            String email
    ) {

        UserModel user = new UserModel();

        user.setId(generator.nextId());
        user.setName(name);
        user.setEmail(email);
        user.setActive(true);
        user.setVersion(1L);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.insert(user);
    }

    protected UserModel createUserFast() {

        UserModel user = new UserModel();

        user.setId(generator.nextId());
        user.setName("Pochita "+ this.generateRandomChars(50).toLowerCase() );
        user.setEmail("pochita" + this.generateRandomChars(51).toLowerCase() + "@gmail.com");
        user.setActive(true);
        user.setVersion(1L);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return userRepository.insert(user);
    }

    protected UrlAccessRuleModel createUrlAccessRule() {
        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        var now = LocalDateTime.now();

        rule.setId(generator.nextId());
        rule.setAssignedByUserId(generator.nextId());
        rule.setUrlId(generator.nextId());
        rule.setExpiresAt(LocalDateTime.now().plusDays(11));
        rule.setActive(true);
        rule.setRuleValue("11");
        rule.setType(UrlAccessRuleTypeEnum.RATE_LIMIT);
        rule.setCreatedAt(now);
        rule.setUpdatedAt(now);

        return urlAccessRuleRepository.insert(rule);
    }

    protected ArrayList<UrlAccessRuleModel> createManyUrlAccessRule(int amount) {
        ArrayList<UrlAccessRuleModel> list = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            UrlAccessRuleModel rule = this.createUrlAccessRule();

            list.add(rule);
        }

        return list;
    }

    protected ArrayList<UserModel> createManyUsers(int amount) {
        ArrayList<UserModel> urs = new ArrayList<>();
        for (int i = 0; i < amount; i++) {

            UserModel user = createUser(
                    "user-" + i,
                    "user-" + i + "@gmail.com"
            );

            urs.add(user);
        }

        return urs;
    }

    protected UrlModel createUrl() {
        UrlModel url = new UrlModel();

        url.setId(generator.nextId());
        url.setShortCode(Base62.encode(this.generator.nextId()));
        url.setDescription("My description");
        url.setFaviconUrl("https://site.com/favicon.ico");
        url.setOriginalUrl("https://example.com/page");
        url.setTitle("Example");
        url.setDomain("example.com");
        url.setStatus(UrlStatusEnum.ACTIVE);
        url.setAccessType(UrlAccessTypeEnum.PUBLIC);
        url.setPasswordHash("secret");
        url.setCustomAlias(true);
        url.setExpiresAt(LocalDateTime.now().plusDays(1));

        return urlRepository.insert(url);
    }

    protected ArrayList<UrlModel> createUrlMany(int amount) {
        ArrayList<UrlModel> urls = new ArrayList<>();

        for (int i = 0; i < amount; i++) {
            UrlModel url = createUrl();

            urls.add(url);
        }

        return urls;
    }

    protected String generateRandomChars(int size) {
        StringBuilder builder = new StringBuilder();

        while (builder.length() < size) {
            builder.append(
                    Base62.encode(generator.nextId())
            );
        }

        return builder.substring(0, size);
    }
}
