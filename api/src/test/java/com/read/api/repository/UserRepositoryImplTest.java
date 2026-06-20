package com.read.api.repository;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import com.read.api.infrastructure.persistence.entity.UserEntity;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryImplTest extends BaseRepositoryTest {

    @BeforeEach
    void setup() {
        template.dropCollection(UserEntity.class);
    }

    @Test
    void should_save_user() {
        UserModel saved = createUser();
        assertNotNull(saved);
        assertNotNull(saved.getId());
    }

    @Test
    void should_insert_user() {
        UserModel user = new UserModel();
        user.setId(this.generator.nextId());
        user.setName("John Doe");
        user.setEmail("john.doe@test.com");
        user.setActive(true);

        UserModel inserted = userRepository.insert(user);

        assertNotNull(inserted);
        assertEquals(user.getId(), inserted.getId());
        assertTrue(userRepository.existsById(inserted.getId()));
    }

    @Test
    void should_find_user_by_id() {
        UserModel saved = createUser();

        Optional<UserModel> found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void should_verify_if_user_exists() {
        UserModel saved = createUser();

        assertTrue(userRepository.existsById(saved.getId()));
    }

    @Test
    void should_delete_user() {
        UserModel saved = createUser();

        int deleted = userRepository.deleteById(saved.getId());

        assertEquals(1, deleted);
        assertFalse(userRepository.existsById(saved.getId()));
    }

    @Test
    void should_find_users_by_name_filter() {
        UserModel saved = createUser();

        UserFilter filter = new UserFilter();
        filter.setName(saved.getName());

        var page = userRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().getFirst().getName()).containsIgnoringCase(saved.getName());
    }

    @Test
    void should_find_users_by_email_filter() {
        UserModel saved = createUser();

        UserFilter filter = new UserFilter();
        filter.setEmail(saved.getEmail());

        var page = userRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
    }

    @Test
    void should_find_users_by_active_filter() {
        UserModel saved = createUser();

        UserFilter filter = new UserFilter();
        filter.setActive(saved.isActive());

        var page = userRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void should_find_user_by_email_ignore_case() {
        UserModel saved = createUser();
        String upperEmail = saved.getEmail().toUpperCase();

        Optional<UserModel> found = userRepository.findByEmailIgnoreCase(upperEmail);

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void should_verify_if_user_exists_by_email_ignore_case() {
        UserModel saved = createUser();
        String upperEmail = saved.getEmail().toUpperCase();

        boolean exists = userRepository.existsByEmailIgnoreCase(upperEmail);

        assertTrue(exists);
    }

    @Test
    void should_verify_if_user_exists_by_name_ignore_case() {
        UserModel saved = createUser();
        String lowerName = saved.getName().toLowerCase();

        boolean exists = userRepository.existsByNameIgnoreCase(lowerName);

        assertTrue(exists);
    }
}