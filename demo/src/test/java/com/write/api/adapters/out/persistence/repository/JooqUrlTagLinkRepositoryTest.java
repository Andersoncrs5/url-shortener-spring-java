package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.Tables;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
class JooqUrlTagLinkRepositoryTest {

    @Autowired
    private HelpRepositoryTest help;

    @Autowired
    private SnowflakeIdGenerator generator;

    @Autowired
    private JooqUrlTagLinkRepository repository;

    @Autowired
    private DSLContext dsl;

    private UserModel user;
    private UrlModel url;
    private UrlTagModel tag;

    @BeforeEach
    void setup() {
        dsl.deleteFrom(Tables.URL_TAG_LINKS).execute();
        user = help.createUser();
        url = help.createUrl(user);
        tag = help.createUrlTag(user);
    }

    @Test
    void shouldInsertTagToUrl() {
        UrlTagLinkModel link = new UrlTagLinkModel();

        link.setUrlId(url.getId());
        link.setTagId(tag.getId());
        link.setSortOrder((short) 1);
        link.setNote("Backend tag");
        link.setPrimaryTag(true);
        link.setCreatedBy(user.getId());

        UrlTagLinkModel saved = repository.insert(link);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUrlId()).isEqualTo(url.getId());
        assertThat(saved.getTagId()).isEqualTo(tag.getId());
        assertThat(saved.getSortOrder()).isEqualTo((short) 1);
        assertThat(saved.getNote()).isEqualTo("Backend tag");
        assertThat(saved.isPrimaryTag()).isTrue();
        assertThat(saved.getCreatedBy()).isEqualTo(user.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUrlTagLink() {
        UrlTagLinkModel link = help.createTagToUrl(user, url, tag);

        link.setSortOrder((short) 5);
        link.setNote("Updated note");
        link.setPrimaryTag(false);

        UrlTagLinkModel updated = repository.save(link);

        assertThat(updated.getSortOrder()).isEqualTo((short) 5);
        assertThat(updated.getNote()).isEqualTo("Updated note");
        assertThat(updated.isPrimaryTag()).isFalse();
    }

    @Test
    void shouldFindUrlTagLinkById() {
        UrlTagLinkModel saved = help.createTagToUrl(user, url, tag);

        Optional<UrlTagLinkModel> result =
                repository.findById(saved.getId());

        assertThat(result).isPresent();

        UrlTagLinkModel found = result.get();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUrlId()).isEqualTo(url.getId());
        assertThat(found.getTagId()).isEqualTo(tag.getId());
    }

    @Test
    void shouldReturnEmptyWhenUrlTagLinkNotFound() {
        Optional<UrlTagLinkModel> result =
                repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteUrlTagLinkById() {
        UrlTagLinkModel saved = help.createTagToUrl(user, url, tag);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
    }

    @Test
    void shouldReturnTrueWhenUrlTagLinkExists() {
        UrlTagLinkModel saved = help.createTagToUrl(user, url, tag);

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUrlTagLinkDoesNotExist() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFailWhenUpdatingNonExistingUrlTagLink() {
        UrlTagLinkModel link = new UrlTagLinkModel();

        link.setId(generator.nextId());
        link.setUrlId(url.getId());
        link.setTagId(tag.getId());

        assertThatThrownBy(() -> repository.save(link))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("UrlTagLink not found");
    }

    @Test
    void shouldDeleteUrlTagLinkByIdAndRemoveItFromDatabase() {
        UrlTagLinkModel saved = help.createTagToUrl(user, url, tag);

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
        assertThat(repository.findById(saved.getId())).isEmpty();
        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void shouldReturnZeroWhenDeletingNonExistingUrlTagLink() {
        int rows = repository.deleteById(generator.nextId());

        assertThat(rows).isZero();
    }

    @Test
    void shouldNotFindUrlTagLinkAfterDelete() {
        UrlTagLinkModel saved = help.createTagToUrl(user, url, tag);

        repository.deleteById(saved.getId());

        Optional<UrlTagLinkModel> result = repository.findById(saved.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateAndThenFindUpdatedUrlTagLink() {
        UrlTagLinkModel link = help.createTagToUrl(user, url, tag);

        link.setSortOrder((short) 3);
        link.setNote("Updated note");
        link.setPrimaryTag(false);

        UrlTagLinkModel updated = repository.save(link);

        Optional<UrlTagLinkModel> result = repository.findById(updated.getId());

        assertThat(result).isPresent();

        UrlTagLinkModel found = result.get();

        assertThat(found.getSortOrder()).isEqualTo((short) 3);
        assertThat(found.getNote()).isEqualTo("Updated note");
        assertThat(found.isPrimaryTag()).isFalse();
    }

    @Test
    void shouldFailWhenInsertingDuplicateUrlTagLinkForSameUrlAndTag() {
        help.createTagToUrl(user, url, tag);

        UrlTagLinkModel duplicate = new UrlTagLinkModel();
        duplicate.setUrlId(url.getId());
        duplicate.setTagId(tag.getId());
        duplicate.setSortOrder((short) 1);
        duplicate.setNote("Duplicate");
        duplicate.setPrimaryTag(false);
        duplicate.setCreatedBy(user.getId());

        assertThatThrownBy(() -> repository.insert(duplicate))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldInsertUrlTagLinkWithNullOptionalFields() {
        UrlTagLinkModel link = new UrlTagLinkModel();

        link.setUrlId(url.getId());
        link.setTagId(tag.getId());
        link.setPrimaryTag(false);
        link.setCreatedBy(user.getId());

        UrlTagLinkModel saved = repository.insert(link);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSortOrder()).isNull();
        assertThat(saved.getNote()).isNull();
        assertThat(saved.isPrimaryTag()).isFalse();
        assertThat(saved.getCreatedBy()).isEqualTo(user.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldReturnTrueAfterInsertAndFalseAfterDeleteForExistsById() {
        UrlTagLinkModel saved = help.createTagToUrl(user, url, tag);

        assertThat(repository.existsById(saved.getId())).isTrue();

        repository.deleteById(saved.getId());

        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void shouldFailWhenSavingUrlTagLinkWithNonExistingId() {
        UrlTagLinkModel link = new UrlTagLinkModel();

        link.setId(generator.nextId());
        link.setUrlId(url.getId());
        link.setTagId(tag.getId());
        link.setSortOrder((short) 1);
        link.setPrimaryTag(true);
        link.setCreatedBy(user.getId());

        assertThatThrownBy(() -> repository.save(link))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("UrlTagLink not found");
    }

}