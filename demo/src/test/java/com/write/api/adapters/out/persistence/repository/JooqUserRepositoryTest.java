package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.help.BaseRepositoryTest;
import com.write.api.core.domain.model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JooqUserRepositoryTest extends BaseRepositoryTest {

    @Autowired private JooqUserRepository repository;

    private UserModel user;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setName("John Doe" + this.generator.nextId());
        user.setEmail("john" + this.generator.nextId() + "@example.com");
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
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        var name = "Jane Doe" + this.generator.nextId();
        UserModel savedUser = repository.insert(user);
        savedUser.setName(name);

        UserModel updatedUser = repository.save(savedUser);

        assertThat(updatedUser.getName()).isEqualTo(name);
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
        boolean exists = repository.existsByEmailIgnoreCase("user"  + this.generator.nextId() + "@gmail.com");

        assertThat(exists).isFalse();
    }

}
