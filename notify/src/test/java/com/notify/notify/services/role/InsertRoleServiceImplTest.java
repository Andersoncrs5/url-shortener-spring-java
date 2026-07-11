package com.notify.notify.services.role;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.roles.services.provider.InsertRoleServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("InsertRoleServiceImpl Unit Tests")
public class InsertRoleServiceImplTest extends BaseServiceTest {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    InsertRoleServiceImpl insertRoleService;

    RoleEntity inputRole;
    RoleEntity outputRole;

    @BeforeEach
    void setUp() {
        inputRole = new RoleEntity();
        // Presumindo propriedades Manifold ou getters/setters comuns da sua RoleEntity
        inputRole.setName("ROLE_ADMIN");

        outputRole = new RoleEntity();
        outputRole.setId(55555L);
        outputRole.setName("ROLE_ADMIN");
    }

    @Nested
    @DisplayName("Validation & Business Rules Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should return failure when role name already exists in database constraint")
        void shouldReturnFailureWhenRoleNameAlreadyExists() {
            // Arrange
            Throwable specificCause = new Throwable("Error integrity violation: uk_roles_name");
            DataIntegrityViolationException exceptionMock = mock(DataIntegrityViolationException.class);
            when(exceptionMock.getMostSpecificCause()).thenReturn(specificCause);

            doThrow(exceptionMock).when(roleRepository).insert(any(RoleEntity.class));

            // Act
            Result<RoleEntity> result = insertRoleService.execute(inputRole);

            // Assert
            assertTrue(result.isFailure());
            assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
            assertEquals("Name already exists", result.getMessage().get());

            verify(roleRepository, times(1)).insert(inputRole);
        }
    }

    @Nested
    @DisplayName("Execution & Persistence Tests")
    class ExecutionTests {

        @Test
        @DisplayName("Should successfully return success result when repository insert returns persisted role")
        void shouldInsertRoleSuccessfully() {
            // Arrange
            when(roleRepository.insert(any(RoleEntity.class))).thenReturn(outputRole);

            // Act
            Result<RoleEntity> result = insertRoleService.execute(inputRole);

            // Assert
            assertTrue(result.isSuccess());
            assertNotNull(result.getValue());
            assertEquals(55555L, result.getValue().getId());
            assertEquals("ROLE_ADMIN", result.getValue().getName());

            verify(roleRepository, times(1)).insert(inputRole);
        }
    }

    @Nested
    @DisplayName("Exception & Infrastructure Resilience")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should wrap unexpected infrastructure runtime exception into a RuntimeException")
        void shouldWrapUnexpectedDatabaseExceptionsInRuntimeException() {
            // Arrange
            doThrow(new RuntimeException("Connection pooling exhausted")).when(roleRepository).insert(any());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                insertRoleService.execute(inputRole);
            });

            assertTrue(exception.getMessage().contains("Connection pooling exhausted"));
            verify(roleRepository, times(1)).insert(inputRole);
        }
    }
}