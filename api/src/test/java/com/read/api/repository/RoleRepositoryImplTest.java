package com.read.api.repository;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.domain.model.RoleModel;
import com.read.api.repository.base.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RoleRepositoryImplTest extends BaseRepositoryTest {

    @Test
    void should_save_role() {
        createRole();
    }

    @Test
    void should_find_role_by_id() {
        RoleModel saved = createRole();

        var found = roleRepository.findById(
                saved.getId()
        );

        assertTrue(found.isPresent());
        assertEquals(
                saved.getId(),
                found.get().getId()
        );
    }

    @Test
    void should_verify_if_role_exists() {

        RoleModel saved = createRole();

        assertTrue(
                roleRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_delete_role() {

        RoleModel saved = createRole();

        int deleted =
                roleRepository.deleteById(
                        saved.getId()
                );

        assertEquals(1, deleted);

        assertFalse(
                roleRepository.existsById(
                        saved.getId()
                )
        );
    }

    @Test
    void should_find_roles_by_name_filter() {

        createRole();

        RoleModel userRole = createRole();
        userRole.setName("USER_ROLE");

        roleRepository.save(userRole);

        RoleFilter filter = new RoleFilter();
        filter.setName("ADMIN");

        var page = roleRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertFalse(page.isEmpty());

        assertThat(
                page.getContent()
                        .getFirst()
                        .getName()
        ).containsIgnoringCase("ADMIN");
    }

    @Test
    void should_find_roles_by_description_filter() {

        RoleModel role = createRole();

        RoleFilter filter = new RoleFilter();
        filter.setDescription(
                role.getDescription()
        );

        roleRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

    }

    @Test
    void should_find_roles_by_active_filter() {

        RoleModel activeRole = createRole();

        RoleModel inactiveRole = createRole();
        inactiveRole.setActive(false);

        roleRepository.save(inactiveRole);

        RoleFilter filter = new RoleFilter();
        filter.setActive(true);

        var page = roleRepository.findAll(
                filter,
                PageRequest.of(0, 10)
        );

        assertTrue(
                page.getContent()
                        .stream()
                        .allMatch(RoleModel::isActive)
        );
    }
}