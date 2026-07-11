package com.notify.notify.services.user;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.user.services.provider.InsertUserServiceImpl;
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

@DisplayName("InsertUserServiceImpl Unit Tests")
public class InsertUserServiceImplTest extends BaseServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    InsertUserServiceImpl insertUserService;

    UserEntity inputUser;

    @BeforeEach
    void setUp() {
        inputUser = new UserEntity();
        inputUser.setName("John Doe");
        inputUser.setEmail("john@notify.com");
        inputUser.setId(9876543210L);
    }

    @Nested
    @DisplayName("Validation & Business Rules Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should return failure when email already exists in system via database constraint")
        void shouldReturnFailureWhenEmailAlreadyExists() {
            Throwable specificCause = new Throwable("Error integrity violation: uk_users_email");
            DataIntegrityViolationException exceptionMock = mock(DataIntegrityViolationException.class);
            when(exceptionMock.getMostSpecificCause()).thenReturn(specificCause);

            doThrow(exceptionMock).when(userRepository).insert(any(UserEntity.class));

            Result<UserEntity> result = insertUserService.execute(inputUser);

            assertTrue(result.isFailure());
            assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
            assertEquals("Email already exists", result.getMessage().get());

            verify(userRepository, times(1)).insert(inputUser);
        }
    }

    @Nested
    @DisplayName("Execution & Persistence Tests")
    class ExecutionTests {

        @Test
        @DisplayName("Should successfully persist user when repository insert succeeds")
        void shouldInsertUserSuccessfully() {
            when(userRepository.insert(any(UserEntity.class))).thenReturn(inputUser);

            Result<UserEntity> result = insertUserService.execute(inputUser);

            assertTrue(result.isSuccess());
            assertNotNull(result.getValue());
            assertEquals(9876543210L, result.getValue().getId());
            assertEquals("John Doe", result.getValue().getName());

            verify(userRepository, times(1)).insert(inputUser);
        }
    }

    @Nested
    @DisplayName("Exception & Infrastructure Resilience")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should wrap unexpected database runtime exception into a RuntimeException")
        void shouldWrapUnexpectedDatabaseExceptionsInRuntimeException() {
            doThrow(new RuntimeException("Database timeout connection")).when(userRepository).insert(any());

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                insertUserService.execute(inputUser);
            });

            assertTrue(exception.getMessage().contains("Database timeout connection"));
        }
    }
}