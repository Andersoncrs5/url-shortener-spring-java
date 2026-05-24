package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
public class JooqUrlTagRepositoryTest {

    @Autowired
    private HelpRepositoryTest help;

    @Autowired private SnowflakeIdGenerator generator;
    @Autowired private JooqUrlTagRepository repository;

    private UserModel user;

    @BeforeEach
    void setup() {
        user = help.createUser();
    }

    @Test
    void shouldInsertUrlTag() {
        UrlTagModel tag = new UrlTagModel();

        tag.setUserId(user.getId());
        tag.setName("Backend");
        tag.setSlug("backend");
        tag.setColor("#000000");
        tag.setDescription("Backend tag");
        tag.setActive(true);

        UrlTagModel saved = repository.insert(tag);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getName()).isEqualTo("Backend");
        assertThat(saved.getSlug()).isEqualTo("backend");
        assertThat(saved.getColor()).isEqualTo("#000000");
        assertThat(saved.getDescription()).isEqualTo("Backend tag");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldInsertChildTag() {
        UrlTagModel parent = new UrlTagModel();

        parent.setUserId(user.getId());
        parent.setName("Backend");
        parent.setSlug("backend");
        parent.setActive(true);

        UrlTagModel parentSaved = repository.insert(parent);

        UrlTagModel child = new UrlTagModel();

        child.setUserId(user.getId());
        child.setName("Java");
        child.setSlug("java");
        child.setParentId(parentSaved.getId());
        child.setActive(true);

        UrlTagModel childSaved = repository.insert(child);

        assertThat(childSaved).isNotNull();
        assertThat(childSaved.getParentId())
                .isEqualTo(parentSaved.getId());
    }

    @Test
    void shouldNotAllowDuplicateSlugForSameUser() {
        UrlTagModel first = new UrlTagModel();

        first.setUserId(user.getId());
        first.setName("Backend");
        first.setSlug("backend");
        first.setActive(true);

        repository.insert(first);

        UrlTagModel second = new UrlTagModel();

        second.setUserId(user.getId());
        second.setName("Backend 2");
        second.setSlug("backend");
        second.setActive(true);

        assertThatThrownBy(() -> repository.insert(second))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldAllowSameSlugForDifferentUsers() {
        UserModel anotherUser = help.createUser();

        UrlTagModel first = new UrlTagModel();

        first.setUserId(user.getId());
        first.setName("Backend");
        first.setSlug("backend");
        first.setActive(true);

        repository.insert(first);

        UrlTagModel second = new UrlTagModel();

        second.setUserId(anotherUser.getId());
        second.setName("Backend");
        second.setSlug("backend");
        second.setActive(true);

        UrlTagModel saved = repository.insert(second);

        assertThat(saved).isNotNull();
    }

    @Test
    void shouldUpdateUrlTag() {
        UrlTagModel saved = help.createUrlTag(user);
        var key = UUID.randomUUID().toString();

        saved.setName("Java" + key);
        saved.setSlug("java" + key);
        saved.setDescription("Java ecosystem"  + key);

        UrlTagModel updated = repository.save(saved);

        assertThat(updated.getName()).isEqualTo("Java" + key);
        assertThat(updated.getSlug()).isEqualTo("java" + key);
        assertThat(updated.getDescription()).isEqualTo("Java ecosystem" + key);
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteUrlTagById() {
        UrlTagModel saved = help.createUrlTag(user);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
    }

    @Test
    void shouldFindUrlTagById() {
        UrlTagModel saved = help.createUrlTag(user);

        Optional<UrlTagModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();

        UrlTagModel tag = result.get();

        assertThat(tag.getId()).isEqualTo(saved.getId());
        assertThat(tag.getUserId()).isEqualTo(user.getId());
        assertThat(tag.getName()).isEqualTo(saved.getName());
        assertThat(tag.getSlug()).isEqualTo(saved.getSlug());
    }

    @Test
    void shouldReturnEmptyWhenUrlTagNotFound() {
        Optional<UrlTagModel> result = repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenUrlTagExistsById() {
        UrlTagModel saved = help.createUrlTag(user);

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUrlTagDoesNotExistById() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

}