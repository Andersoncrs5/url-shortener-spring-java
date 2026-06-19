package com.read.api.repository.base;

import com.read.api.TestcontainersConfiguration;
import com.read.api.domain.enums.*;
import com.read.api.domain.model.*;
import com.read.api.domain.repository.*;
import com.read.api.domain.utils.Base62;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class BaseRepositoryTest {
    @Autowired protected SnowflakeIdGenerator generator;
    @Autowired protected MongoTemplate template;

    @Autowired protected OutboxEventRepository outboxEventRepository;
    @Autowired protected RoleRepository roleRepository;
    @Autowired protected UserRepository userRepository;
    @Autowired protected UrlAccessRuleRepository urlAccessRuleRepository;
    @Autowired protected UrlRedirectRuleRepository urlRedirectRuleRepository;
    @Autowired protected UrlTagRepository urlTagRepository;
    @Autowired protected UrlRepository urlRepository;

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

    protected UrlModel addTagInUrl(UrlModel url, List<String> tags) {
        for (String tag : tags) {
            url.addTag(tag);
        }

        return urlRepository.save(url);
    }


    protected UrlTagModel createUrlTag() {
        UrlTagModel model = new UrlTagModel();
        model.setId(generator.nextId());
        model.setUserId(generator.nextId());
        model.setName("Desenvolvimento_" + generator.nextId());
        model.setSlug("dev-" + generator.nextId());
        model.setColor("#FF5733");
        model.setDescription("Tag voltada para links de ambiente dev");
        model.setParentId(null);
        model.setActive(true);

        return urlTagRepository.save(model);
    }

    protected UrlTagModel createUrlTag(String name, String slug, Long userId) {
        UrlTagModel model = new UrlTagModel();
        model.setId(generator.nextId());
        model.setUserId(userId);
        model.setName(name);
        model.setSlug(slug);
        model.setColor("#33MN99");
        model.setDescription("Tag customizada para testes");
        model.setParentId(null);
        model.setActive(true);

        return urlTagRepository.save(model);
    }

    protected UrlRedirectRuleModel createUrlRedirectRule() {
        var entity = new UrlRedirectRuleModel();

        entity.setId(generator.nextId());
        entity.setUrlId(generator.nextId());
        entity.setCountryCode("BR");
        entity.setRegion("PI");
        entity.setContinent(ContinentEnum.SOUTH_AMERICA);
        entity.setOs(OperatingSystemEnum.LINUX);
        entity.setBrowser(BrowserEnum.CHROME);
        entity.setMatchType(MatchTypeEnum.EXACT);
        entity.setRedirectUrl("https://google.com");
        entity.setRuleHash(UUID.randomUUID().toString());
        entity.setPriority(1);
        entity.setActive(true);

        return urlRedirectRuleRepository.insert(
                entity
        );
    }

    protected UrlAccessRuleModel createUrlAccessRule() {

        UrlAccessRuleModel model =
                new UrlAccessRuleModel();

        model.setId(generator.nextId());
        model.setUrlId(generator.nextId());
        model.setAssignedByUserId(generator.nextId());

        model.setType(
                UrlAccessRuleTypeEnum.MAX_CLICKS
        );

        model.setRuleValue(
                "user@gmail.com"
        );

        model.setActive(true);

        model.setExpiresAt(
                LocalDateTime.now().plusDays(30)
        );

        return urlAccessRuleRepository.insert(
                model
        );
    }

    protected UserModel createUser() {
        UserModel user = new UserModel();
        user.setId(generator.nextId());
        user.setName("Anderson" + generateRandomChars(10));
        user.setEmail("anderson" + generateRandomChars(10) + "@test.com");
        user.setActive(true);

        UserModel saved = userRepository.save(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(user.getName(), saved.getName());
        assertEquals(user.getEmail(), saved.getEmail());

        return saved;
    }

    protected OutboxEventModel createOutboxEvent() {

        OutboxEventModel event = new OutboxEventModel();

        event.setId(generator.nextId());
        event.setAggregateId(generator.nextId());
        event.setAggregateType(AggregateTypeEnum.USER);
        event.setEventType(EventTypeEnum.CREATED);
        event.setTopic(TopicEnum.USER_CREATED);
        event.setStatus(OutboxStatusEnum.PENDING);

        event.setPayload("""
        {
          "userId": 1
        }
        """);

        event.setRetryCount(0);

        OutboxEventModel saved =
                outboxEventRepository.save(event);

        assertNotNull(saved);
        assertNotNull(saved.getId());

        assertEquals(
                event.getAggregateId(),
                saved.getAggregateId()
        );

        assertEquals(
                event.getStatus(),
                saved.getStatus()
        );

        return saved;
    }

    protected RoleModel createRole() {

        RoleModel role = new RoleModel();

        role.setId(generator.nextId());
        role.setName("ADMIN_" + generateRandomChars(8));
        role.setDescription("Administrator Role");
        role.setActive(true);

        RoleModel saved = roleRepository.save(role);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(role.getName(), saved.getName());
        assertEquals(role.getDescription(), saved.getDescription());

        return saved;
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
