package com.read.api.application.usecase.services.role;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.role.SaveRoleUseCaseImpl;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SaveRoleUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private SaveRoleUseCaseImpl useCase;

    @Test
    void should_save_role_successfully() {

        RoleModel role = new RoleModel();

        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator");
        role.setActive(true);

        when(repository.save(role))
                .thenReturn(role);

        var result = useCase.execute(role);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());

        assertEquals(role.getId(), result.getValue().getId());
        assertEquals(role.getName(), result.getValue().getName());
        assertEquals(role.getDescription(), result.getValue().getDescription());

        verify(repository).save(role);
    }
}