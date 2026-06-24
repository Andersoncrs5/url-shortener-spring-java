package com.read.api.application.usecase.services.role;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.role.DeleteRoleByIdUseCaseImpl;
import com.read.api.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteRoleByIdUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private DeleteRoleByIdUseCaseImpl useCase;

    @Test
    void should_delete_role_successfully() {

        when(repository.deleteById(1L))
                .thenReturn(1);

        var result = useCase.execute(1L);

        assertTrue(result.isSuccess());

        verify(repository).deleteById(1L);
    }

    @Test
    void should_return_failure_when_role_not_found() {

        when(repository.deleteById(1L))
                .thenReturn(0);

        var result = useCase.execute(1L);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Role not found", result.getMessage());

        verify(repository).deleteById(1L);
    }
}