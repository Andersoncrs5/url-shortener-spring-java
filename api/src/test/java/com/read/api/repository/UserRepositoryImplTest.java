package com.read.api.repository;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryImplTest extends BaseRepositoryTest {

    @Test
    void should_save_user() {
        this.createUser();
    }

    @Test
    void should_find_user_by_id() {
        UserModel saved = createUser();

        var found = userRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void should_verify_if_user_exists() {
        UserModel saved = createUser();

        assertTrue(
                userRepository.existsById(saved.getId())
        );
    }

    @Test
    void should_delete_user() {
        UserModel saved = createUser();

        int deleted = userRepository.deleteById(
                saved.getId()
        );

        assertEquals(1, deleted);

        assertFalse(
                userRepository.existsById(saved.getId())
        );
    }

    @Test
    void should_find_users_by_name_filter() {
        userRepository.save(createUser());
        UserModel maria = createUser();
        maria.setName("Maria");
        maria.setEmail("maria@test.com");

        userRepository.save(maria);
        UserFilter filter = new UserFilter();
        filter.setName("Anderson");
        maria.setEmail("anderson@test.com");

        var page = userRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent().getFirst().getName()).containsIgnoringCase("Anderson");
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
}