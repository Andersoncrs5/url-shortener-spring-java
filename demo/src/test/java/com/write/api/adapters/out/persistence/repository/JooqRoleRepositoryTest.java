package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.BaseRepositoryTest;
import com.write.api.core.domain.model.RoleModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JooqRoleRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private JooqRoleRepository repository;

    @Test
    void shouldInsertRole() {
        this.help.createRole();
    }

    @Test
    void shouldUpdateRole() {
        RoleModel saved = this.help.createRole();

        String name = "ROLE_SUPPORT_" + this.help.generateRandomChars(5).toUpperCase();

        saved.setName(name);
        saved.setDescription("Support team");
        saved.setActive(false);

        RoleModel updated = repository.save(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(saved.getId());
        assertThat(updated.getName()).isEqualTo(name);
        assertThat(updated.getDescription()).isEqualTo("Support team");
        assertThat(updated.isActive()).isFalse();
        assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteRoleById() {
        RoleModel saved = this.help.createRole();

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);

        Optional<RoleModel> result = repository.findById(saved.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindRoleById() {
        RoleModel saved = this.help.createRole();

        Optional<RoleModel> result = repository.findById(saved.getId());

        assertThat(result).isPresent();

        RoleModel role = result.get();
        assertThat(role.getId()).isEqualTo(saved.getId());
        assertThat(role.getName()).isEqualTo(saved.getName());
        assertThat(role.isActive()).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenRoleNotFound() {
        Optional<RoleModel> result = repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenRoleExistsById() {
        RoleModel saved = this.help.createRole();

        boolean exists = repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenRoleDoesNotExistById() {
        boolean exists = repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueWhenRoleNameExistsIgnoreCase() {
        RoleModel saved = this.help.createRole();

        boolean exists = repository.existsByNameIgnoreCase(saved.getName());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenRoleNameDoesNotExistIgnoreCase() {
        boolean exists = repository.existsByNameIgnoreCase("ROLE_UNKNOWN");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldNotAllowDuplicateRoleName() {
        RoleModel saved = this.help.createRole();

        RoleModel duplicate = buildRole(saved.getName(), "Another description", true);

        assertThatThrownBy(() -> repository.insert(duplicate))
                .isInstanceOf(Exception.class);
    }

    @Test
    void shouldReturnTrueForCaseInsensitiveNameCheck() {
        RoleModel saved = this.help.createRole();

        boolean exists = repository.existsByNameIgnoreCase(saved.getName().toLowerCase());

        assertThat(exists).isTrue();
    }

    private RoleModel buildRole(String name, String description, boolean active) {
        RoleModel role = new RoleModel();
        role.setName(name);
        role.setDescription(description);
        role.setActive(active);
        return role;
    }
}