package com.notify.notify.services.role;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.roles.services.provider.DeleteRoleByIdServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DeleteRoleByIdServiceImpl Unit Tests")
public class DeleteRoleByIdServiceImplTest extends BaseServiceTest {

    @Mock
    RoleRepository repository;

    @InjectMocks
    DeleteRoleByIdServiceImpl deleteRoleByIdService;

    Long existingRoleId;
    Long nonExistingRoleId;

    @BeforeEach
    void setUp() {
        existingRoleId = 1L;
        nonExistingRoleId = 999L;
    }

    @Nested
    @DisplayName("Success Scenario")
    class SuccessTests {

        @Test
        @DisplayName("Should return success when role is found and deleted")
        void shouldDeleteRoleSuccessfully() {

            when(repository.deleteAndCount(existingRoleId)).thenReturn(1);

            Result<Void> result = deleteRoleByIdService.execute(existingRoleId);

            assertTrue(result.isSuccess());
            verify(repository, times(1)).deleteAndCount(existingRoleId);
        }
    }

    @Nested
    @DisplayName("Failure Scenario")
    class FailureTests {

        @Test
        @DisplayName("Should return NOT_FOUND when repository returns zero rows affected")
        void shouldReturnNotFoundWhenRoleDoesNotExist() {

            when(repository.deleteAndCount(nonExistingRoleId)).thenReturn(0);

            Result<Void> result = deleteRoleByIdService.execute(nonExistingRoleId);

            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertTrue(result.getErrors().contains("Role not found"));
            verify(repository, times(1)).deleteAndCount(nonExistingRoleId);
        }

        @Test
        @DisplayName("Should return NOT_FOUND even if repository returns a negative number by anomaly")
        void shouldReturnNotFoundWhenRepositoryReturnsNegative() {

            when(repository.deleteAndCount(nonExistingRoleId)).thenReturn(-1);


            Result<Void> result = deleteRoleByIdService.execute(nonExistingRoleId);


            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Exception Scenarios")
    class ExceptionTests {

        @Test
        @DisplayName("Should propagate runtime exceptions thrown by the repository")
        void shouldPropagateExceptions() {

            when(repository.deleteAndCount(existingRoleId))
                    .thenThrow(new RuntimeException("Database connectivity failure"));


            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                deleteRoleByIdService.execute(existingRoleId);
            });

            assertEquals("Database connectivity failure", exception.getMessage());
            verify(repository, times(1)).deleteAndCount(existingRoleId);
        }
    }
}