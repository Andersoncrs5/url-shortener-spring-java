package com.notify.notify.services.userRole;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import com.notify.notify.modules.userRole.repository.UserRoleRepository;
import com.notify.notify.modules.userRole.services.provider.InsertUserRoleServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import com.notify.notify.utils.database.DatabaseConstraintHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayName("InsertUserRoleServiceImpl Unit Tests")
public class InsertUserRoleServiceImplTest extends BaseServiceTest {

    @Mock UserRoleRepository userRoleRepository;
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;

    @InjectMocks
    InsertUserRoleServiceImpl insertUserRoleService;

    UserRoleEntity inputUserRole;
    UserEntity mockUser;
    RoleEntity mockRole;

    @BeforeEach
    void setUp() {
        inputUserRole = new UserRoleEntity();
        inputUserRole.setId(1L);
        inputUserRole.setUserId(10L);
        inputUserRole.setRoleId(55L);

        mockUser = new UserEntity();
        mockUser.setId(10L);
        mockUser.setName("John Doe");


        mockRole = new RoleEntity();
        mockRole.setId(55L);
        mockRole.setName("ROLE_ADMIN");
    }

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should process execution in correct order and update user successfully")
        void shouldInsertUserRoleSuccessfully() {

            when(userRoleRepository.insert(any(UserRoleEntity.class))).thenReturn(inputUserRole);
            when(roleRepository.findById(55L)).thenReturn(Optional.of(mockRole));
            when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));
            when(userRepository.update(any(UserEntity.class))).thenReturn(mockUser);


            Result<UserRoleEntity> result = insertUserRoleService.execute(inputUserRole);


            assertTrue(result.isSuccess());
            assertFalse(result.isFailure());
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals(inputUserRole, result.getValue());


            InOrder inOrder = inOrder(userRoleRepository, roleRepository, userRepository);


            inOrder.verify(userRoleRepository, times(1)).insert(inputUserRole);


            inOrder.verify(roleRepository, times(1)).findById(55L);


            inOrder.verify(userRepository, times(1)).findById(10L);


            inOrder.verify(userRepository, times(1)).update(argThat(user ->
                    user.getId().equals(10L) && user.getRoles().contains("ROLE_ADMIN")
            ));


            verifyNoMoreInteractions(userRoleRepository, roleRepository, userRepository);
        }
    }

    @Nested
    @DisplayName("Business Rule & Not Found Scenarios")
    class NotFoundScenarios {

        @Test
        @DisplayName("Should return Not Found and stop execution if User is missing")
        void shouldReturnNotFoundWhenUserIsMissing() {

            when(userRoleRepository.insert(any(UserRoleEntity.class))).thenReturn(inputUserRole);
            when(roleRepository.findById(55L)).thenReturn(Optional.of(mockRole));
            when(userRepository.findById(10L)).thenReturn(Optional.empty());


            Result<UserRoleEntity> result = insertUserRoleService.execute(inputUserRole);


            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("User not found", result.getMessage().orElse(null));


            InOrder inOrder = inOrder(userRoleRepository, roleRepository, userRepository);
            inOrder.verify(userRoleRepository).insert(inputUserRole);
            inOrder.verify(roleRepository).findById(55L);
            inOrder.verify(userRepository).findById(10L);


            verify(userRepository, never()).update(any());
        }

        @Test
        @DisplayName("Should return Not Found and stop execution if Role is missing")
        void shouldReturnNotFoundWhenRoleIsMissing() {

            when(userRoleRepository.insert(any(UserRoleEntity.class))).thenReturn(inputUserRole);
            when(roleRepository.findById(55L)).thenReturn(Optional.empty());
            when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));


            Result<UserRoleEntity> result = insertUserRoleService.execute(inputUserRole);


            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("Role not found", result.getMessage().orElse(null));


            verify(userRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Data Integrity & Constraint Scenarios")
    class ConstraintScenarios {

        @Test
        @DisplayName("Should return failure Conflict when composite unique key constraint is violated")
        void shouldReturnConflictWhenCompositeConstraintViolated() {

            Throwable specificCause = new Throwable("Duplicate entry for key 'uk_user_roles_user_role'");
            DataIntegrityViolationException exceptionMock = mock(DataIntegrityViolationException.class);
            when(exceptionMock.getMostSpecificCause()).thenReturn(specificCause);

            doThrow(exceptionMock).when(userRoleRepository).insert(any(UserRoleEntity.class));


            Result<UserRoleEntity> result = insertUserRoleService.execute(inputUserRole);


            assertTrue(result.isFailure());
            assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
            assertEquals("This role is already assigned to the user", result.getMessage().orElse(null));


            verify(userRoleRepository, times(1)).insert(inputUserRole);
            verifyNoInteractions(roleRepository, userRepository);
        }

        @Test
        @DisplayName("Should fallback to DatabaseConstraintHandler when specific cause message is null")
        void shouldFallbackToHandlerWhenMessageIsNull() {

            Throwable specificCause = mock(Throwable.class);
            when(specificCause.getMessage()).thenReturn(null);

            DataIntegrityViolationException exceptionMock = mock(DataIntegrityViolationException.class);
            when(exceptionMock.getMostSpecificCause()).thenReturn(specificCause);

            doThrow(exceptionMock).when(userRoleRepository).insert(any(UserRoleEntity.class));

            Result<UserRoleEntity> handlerFallbackResult = Result.failure("Generic database violation", HttpStatus.BAD_REQUEST);

            try (MockedStatic<DatabaseConstraintHandler> mockedHandler = mockStatic(DatabaseConstraintHandler.class)) {
                mockedHandler.when(() -> DatabaseConstraintHandler.handle(exceptionMock)).thenReturn(handlerFallbackResult);


                Result<UserRoleEntity> result = insertUserRoleService.execute(inputUserRole);


                assertTrue(result.isFailure());
                assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
                assertEquals("Generic database violation", result.getMessage().orElse(null));
            }
            verifyNoInteractions(roleRepository, userRepository);
        }
    }

    @Nested
    @DisplayName("Exception & Failure Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should throw RuntimeException when repository throws an unexpected runtime exception")
        void shouldThrowRuntimeExceptionOnUnexpectedError() {

            NullPointerException unexpectedException = new NullPointerException("Database connection dropped unexpectedly");
            doThrow(unexpectedException).when(userRoleRepository).insert(any(UserRoleEntity.class));


            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                insertUserRoleService.execute(inputUserRole);
            });

            assertInstanceOf(NullPointerException.class, exception.getCause());
            assertEquals("java.lang.NullPointerException: Database connection dropped unexpectedly", exception.getMessage());


            verify(userRoleRepository, times(1)).insert(inputUserRole);
            verifyNoInteractions(roleRepository, userRepository);
        }
    }
}