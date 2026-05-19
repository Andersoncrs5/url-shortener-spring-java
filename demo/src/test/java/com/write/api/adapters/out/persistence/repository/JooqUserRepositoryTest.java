package com.write.api.adapters.out.persistence.repository;

import com.write.api.TestcontainersConfiguration;
import com.write.api.adapters.out.persistence.help.HelpRepositoryTest;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
@AutoConfigureMockMvc
public class JooqUserRepositoryTest {

    private HelpRepositoryTest help;

    @Autowired private DSLContext dsl;
    @Autowired private SnowflakeIdGenerator generator;
    @Autowired private JooqUserRepository repository;

    private UserModel user;

    @BeforeEach
    void setUp() {
        help = new HelpRepositoryTest(generator, repository);

        dsl.deleteFrom(org.jooq.impl.DSL.table("users")).execute();

        user = new UserModel();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("54356435645625467245425");
        user.setActive(true);
    }

    @Test
    void shouldFailTheDeleteUserById() {
        int deleted = this.repository.deleteById(generator.nextId());

        assertThat(deleted).isEqualTo(0);
    }

    @Test
    void shouldDeleteUserById() {
        UserModel userModel = this.help.createUser();
        int deleted = this.repository.deleteById(userModel.getId());

        assertThat(deleted).isEqualTo(1);
    }

    @Test
    void shouldInsertUserWithSnowflakeId() {
        UserModel savedUser = repository.insert(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();

        var count = dsl.fetchCount(org.jooq.impl.DSL.table("users"));
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UserModel savedUser = repository.insert(user);
        savedUser.setName("Jane Doe");

        UserModel updatedUser = repository.save(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Jane Doe");
        assertThat(updatedUser.getUpdatedAt()).isAfter(savedUser.getCreatedAt());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        user.setId(999L);

        assertThrows(IllegalStateException.class, () -> {
            repository.save(user);
        });
    }

    @Test
    void shouldReturnTrueWhenGetByEmail() {
        UserModel user = this.help.createUser();

        boolean exists = repository.existsByEmailIgnoreCase(user.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenGetByEmail() {
        boolean exists = repository.existsByEmailIgnoreCase("user543534534543@gmail.com");

        assertThat(exists).isFalse();
    }

}
