package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
class JooqUserRoleRepositoryTest {

    @Autowired
    private HelpRepositoryTest help;

    @Autowired
    private DSLContext dsl;

    @Autowired
    private SnowflakeIdGenerator generator;

    @Autowired
    private JooqUserRoleRepository repository;

    private UserModel user;
    private UserModel assignedBy;
    private RoleModel role;

    @BeforeEach
    void setUp() {
        user = help.createUser();
        assignedBy = help.createUser();
        role = help.createRole();
    }

    @Test
    void shouldInsertUserRole() {
        UserRoleModel model = new UserRoleModel();
        model.setUserId(user.getId());
        model.setRoleId(role.getId());
        model.setAssignedByUserId(assignedBy.getId());

        UserRoleModel saved = repository.insert(model);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUserId()).isEqualTo(user.getId());
        assertThat(saved.getRoleId()).isEqualTo(role.getId());
        assertThat(saved.getAssignedByUserId()).isEqualTo(assignedBy.getId());
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateUserRole() {
        UserRoleModel saved = help.createUserRole(
                user,
                role,
                assignedBy
        );

        UserModel anotherUser = help.createUser();

        saved.setAssignedByUserId(anotherUser.getId());

        UserRoleModel updated = repository.save(saved);

        assertThat(updated).isNotNull();
        assertThat(updated.getAssignedByUserId())
                .isEqualTo(anotherUser.getId());
    }

    @Test
    void shouldDeleteUserRoleById() {
        UserRoleModel saved = help.createUserRole(
                user,
                role,
                assignedBy
        );

        int rows = repository.deleteById(saved.getId());

        assertThat(rows).isEqualTo(1);
        assertThat(repository.findById(saved.getId()))
                .isEmpty();
    }

    @Test
    void shouldReturnZeroWhenDeleteNonExistentUserRole() {
        int rows = repository.deleteById(
                generator.nextId()
        );

        assertThat(rows).isEqualTo(0);
    }

    @Test
    void shouldFindUserRoleById() {
        UserRoleModel saved = help.createUserRole(
                user,
                role,
                assignedBy
        );

        Optional<UserRoleModel> result =
                repository.findById(saved.getId());

        assertThat(result).isPresent();

        UserRoleModel found = result.get();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getUserId()).isEqualTo(user.getId());
        assertThat(found.getRoleId()).isEqualTo(role.getId());
        assertThat(found.getAssignedByUserId())
                .isEqualTo(assignedBy.getId());
    }

    @Test
    void shouldReturnEmptyWhenUserRoleNotFound() {
        Optional<UserRoleModel> result =
                repository.findById(generator.nextId());

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenUserRoleExists() {
        UserRoleModel saved = help.createUserRole(
                user,
                role,
                assignedBy
        );

        boolean exists =
                repository.existsById(saved.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserRoleDoesNotExist() {
        boolean exists =
                repository.existsById(generator.nextId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUserRole() {
        UserRoleModel model = new UserRoleModel();

        model.setId(generator.nextId());
        model.setUserId(user.getId());
        model.setRoleId(role.getId());
        model.setAssignedByUserId(assignedBy.getId());

        assertThrows(
                IllegalStateException.class,
                () -> repository.save(model)
        );
    }
}