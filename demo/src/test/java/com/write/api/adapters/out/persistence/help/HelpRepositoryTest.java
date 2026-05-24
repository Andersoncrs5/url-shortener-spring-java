package com.write.api.adapters.out.persistence.help;

import com.write.api.adapters.out.persistence.repository.JooqUrlRepository;
import com.write.api.adapters.out.persistence.repository.JooqUrlTagRepository;
import com.write.api.adapters.out.persistence.repository.JooqUserRepository;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Service
@RequiredArgsConstructor
public class HelpRepositoryTest {

    private final SnowflakeIdGenerator generator;
    private final JooqUserRepository repository;
    private final JooqUrlTagRepository tagRepository;
    private final JooqUrlRepository urlRepository;

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

}