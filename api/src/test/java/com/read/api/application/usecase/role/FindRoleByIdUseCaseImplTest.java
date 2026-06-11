package com.read.api.application.usecase.role;

import com.read.api.application.usecase.impl.role.FindRoleByIdUseCaseImpl;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindRoleByIdUseCaseImplTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private FindRoleByIdUseCaseImpl useCase;

    @Test
    void should_return_role_when_found() {

        RoleModel role = new RoleModel();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrator");

        when(repository.findById(1L))
                .thenReturn(Optional.of(role));

        var result = useCase.execute(1L);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(role.getId(), result.getValue().getId());
        assertEquals(role.getName(), result.getValue().getName());

        verify(repository).findById(1L);
    }

    @Test
    void should_return_failure_when_role_not_found() {

        when(repository.findById(1L))
                .thenReturn(Optional.empty());

        var result = useCase.execute(1L);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Role not found", result.getMessage());

        verify(repository).findById(1L);
    }
}