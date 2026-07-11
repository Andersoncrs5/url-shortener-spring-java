package com.notify.notify.services.role;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.roles.services.provider.SyncRoleServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import com.notify.notify.utils.database.DatabaseConstraintHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SyncRoleService Unit Tests")
public class SyncRoleServiceTest extends BaseServiceTest {

    @Mock
    RoleRepository repository;

    @InjectMocks
    SyncRoleServiceImpl syncRoleService;

    RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_ADMIN");
        roleEntity.setDescription("Administrator access");
        roleEntity.setActive(true);
    }

    @Nested
    @DisplayName("Success Scenario")
    class SuccessTests {

        @Test
        @DisplayName("Should update role successfully and return positive result")
        void shouldSaveRoleSuccessfully() {
            when(repository.update(roleEntity)).thenReturn(roleEntity);

            Result<RoleEntity> result = syncRoleService.execute(roleEntity);

            assertTrue(result.isSuccess());
            assertEquals(roleEntity, result.getValue());
            verify(repository, times(1)).update(roleEntity);
        }
    }

    @Nested
    @DisplayName("Data Integrity & Constraint Violation Scenarios")
    class DataIntegrityTests {

        @Test
        @DisplayName("Should return FORBIDDEN with custom message when unique constraint 'uk_roles_name' is violated")
        void shouldReturnForbiddenWhenRoleNameAlreadyExists() {
            Throwable cause = new Throwable("Error: duplicate key value violates unique constraint \"uk_roles_name\"");
            DataIntegrityViolationException exception = new DataIntegrityViolationException("Database error", cause);

            when(repository.update(roleEntity)).thenThrow(exception);

            Result<RoleEntity> result = syncRoleService.execute(roleEntity);

            assertTrue(result.isFailure());
            assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
            assertTrue(result.getErrors().contains("Name already exists"));
            verify(repository, times(1)).update(roleEntity);
        }

        @Test
        @DisplayName("Should delegate to DatabaseConstraintHandler when specific cause message is null")
        void shouldDelegateToHandlerWhenMostSpecificCauseIsNull() {

            DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);
            when(exception.getMostSpecificCause()).thenReturn(exception);
            when(exception.getMessage()).thenReturn(null);

            when(repository.update(roleEntity)).thenThrow(exception);

            Result<RoleEntity> expectedHandlerResult = Result.failure("Generic constraint violation", HttpStatus.INTERNAL_SERVER_ERROR);


            try (MockedStatic<DatabaseConstraintHandler> handlerMock = mockStatic(DatabaseConstraintHandler.class)) {
                handlerMock.when(() -> DatabaseConstraintHandler.handle(exception)).thenReturn(expectedHandlerResult);


                Result<RoleEntity> result = syncRoleService.execute(roleEntity);


                assertNotNull(result);
                assertFalse(result.isSuccess());
                handlerMock.verify(() -> DatabaseConstraintHandler.handle(exception), times(1));
            }
        }

        @Test
        @DisplayName("Should delegate to DatabaseConstraintHandler when constraint is unknown")
        void shouldDelegateToHandlerWhenConstraintIsUnknown() {

            Throwable cause = new Throwable("fk_other_constraint_error");
            DataIntegrityViolationException exception = new DataIntegrityViolationException("Database error", cause);

            when(repository.update(roleEntity)).thenThrow(exception);

            Result<RoleEntity> expectedHandlerResult = Result.failure("Handled foreign key error", HttpStatus.INTERNAL_SERVER_ERROR);

            try (MockedStatic<DatabaseConstraintHandler> handlerMock = mockStatic(DatabaseConstraintHandler.class)) {
                handlerMock.when(() -> DatabaseConstraintHandler.handle(exception)).thenReturn(expectedHandlerResult);


                Result<RoleEntity> result = syncRoleService.execute(roleEntity);


                assertNotNull(result);
                handlerMock.verify(() -> DatabaseConstraintHandler.handle(exception), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Generic Exception Scenarios")
    class GenericExceptionTests {

        @Test
        @DisplayName("Should throw RuntimeException when an unexpected error occurs")
        void shouldThrowRuntimeExceptionOnUnexpectedError() {
            NullPointerException unexpectedException = new NullPointerException("Null reference inside driver");
            when(repository.update(roleEntity)).thenThrow(unexpectedException);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                syncRoleService.execute(roleEntity);
            });

            assertEquals(unexpectedException, exception.getCause());
        }
    }
}