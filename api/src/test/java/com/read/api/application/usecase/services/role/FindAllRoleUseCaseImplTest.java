package com.read.api.application.usecase.services.role;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.role.FindAllRoleUseCaseImpl;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindAllRoleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private FindAllRoleUseCaseImpl useCase;

    @Test
    void should_return_all_roles() {

        RoleModel role1 = new RoleModel();
        role1.setId(1L);
        role1.setName("ADMIN");

        RoleModel role2 = new RoleModel();
        role2.setId(2L);
        role2.setName("USER");

        RoleFilter filter = new RoleFilter();
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RoleModel> expectedPage =
                new PageImpl<>(
                        List.of(role1, role2),
                        pageable,
                        2
                );

        when(repository.findAll(filter, pageable))
                .thenReturn(expectedPage);

        var result = useCase.execute(
                filter,
                pageable
        );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        assertEquals("ADMIN",
                result.getContent().get(0).getName());

        assertEquals("USER",
                result.getContent().get(1).getName());

        verify(repository)
                .findAll(filter, pageable);
    }
}