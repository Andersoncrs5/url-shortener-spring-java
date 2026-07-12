package com.notify.notify.services.userRole;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import com.notify.notify.modules.userRole.repository.UserRoleRepository;
import com.notify.notify.modules.userRole.services.provider.DeleteUserRoleServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayName("DeleteUserRoleServiceImpl Unit Tests")
public class DeleteUserRoleServiceImplTest extends BaseServiceTest {

    @Mock UserRoleRepository userRoleRepository;
    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;

    @InjectMocks
    DeleteUserRoleServiceImpl deleteUserRoleService;

    UserRoleEntity mockUserRole;
    UserEntity mockUser;
    RoleEntity mockRole;
    Long relationId = 100L;

    @BeforeEach
    void setUp() {
        mockUserRole = new UserRoleEntity();
        mockUserRole.setId(relationId);
        mockUserRole.setUserId(10L);
        mockUserRole.setRoleId(55L);

        mockUser = new UserEntity();
        mockUser.setId(10L);
        mockUser.setName("John Doe");
        mockUser.addRole("ROLE_ADMIN");
        mockUser.addRole("ROLE_USER");

        mockRole = new RoleEntity();
        mockRole.setId(55L);
        mockRole.setName("ROLE_ADMIN");
    }

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should delete user role mapping and update user roles set in the correct order")
        void shouldDeleteUserRoleSuccessfully() {

            when(userRoleRepository.findById(relationId)).thenReturn(Optional.of(mockUserRole));
            when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));
            when(roleRepository.findById(55L)).thenReturn(Optional.of(mockRole));
            when(userRoleRepository.deleteAndCount(relationId)).thenReturn(1);
            when(userRepository.update(any(UserEntity.class))).thenReturn(mockUser);

            Result<Void> result = deleteUserRoleService.execute(relationId);

            assertTrue(result.isSuccess());
            assertFalse(result.isFailure());
            assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
            assertNull(result.getValue());

            InOrder inOrder = inOrder(userRoleRepository, roleRepository, userRepository);

            inOrder.verify(userRoleRepository).findById(relationId);
            inOrder.verify(userRepository).findById(10L);
            inOrder.verify(roleRepository).findById(55L);
            inOrder.verify(userRoleRepository).deleteAndCount(relationId);
            inOrder.verify(userRepository).update(argThat(user ->
                    user.getId().equals(10L) &&
                            !user.getRoles().contains("ROLE_ADMIN") &&
                            user.getRoles().contains("ROLE_USER")
            ));

            verifyNoMoreInteractions(userRoleRepository, roleRepository, userRepository);
        }
    }

    @Nested
    @DisplayName("Not Found & Business Rule Scenarios")
    class NotFoundScenarios {

        @Test
        @DisplayName("Should return Not Found when mapping relationship does not exist")
        void shouldReturnNotFoundWhenRelationMissing() {
            when(userRoleRepository.findById(relationId)).thenReturn(Optional.empty());

            Result<Void> result = deleteUserRoleService.execute(relationId);

            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("User Role not found", result.getMessage().orElse(null));

            verify(userRoleRepository, times(1)).findById(relationId);
            verifyNoMoreInteractions(userRoleRepository);
            verifyNoInteractions(userRepository, roleRepository);
        }

        @Test
        @DisplayName("Should return Not Found and stop when associated User does not exist")
        void shouldReturnNotFoundWhenUserMissing() {

            when(userRoleRepository.findById(relationId)).thenReturn(Optional.of(mockUserRole));
            when(userRepository.findById(10L)).thenReturn(Optional.empty());


            Result<Void> result = deleteUserRoleService.execute(relationId);


            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("User not found", result.getMessage().orElse(null));

            verify(userRoleRepository, never()).deleteAndCount(anyLong());
            verify(userRepository, never()).update(any());
        }

        @Test
        @DisplayName("Should return Not Found and stop when associated Role does not exist")
        void shouldReturnNotFoundWhenRoleMissing() {

            when(userRoleRepository.findById(relationId)).thenReturn(Optional.of(mockUserRole));
            when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));
            when(roleRepository.findById(55L)).thenReturn(Optional.empty());

            Result<Void> result = deleteUserRoleService.execute(relationId);

            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("Role not found", result.getMessage().orElse(null));

            verify(userRoleRepository, never()).deleteAndCount(anyLong());
            verify(userRepository, never()).update(any());
        }

        @Test
        @DisplayName("Should return Not Found if deleteAndCount returns 0 (Concurrency check)")
        void shouldReturnNotFoundWhenCountIsZero() {

            when(userRoleRepository.findById(relationId)).thenReturn(Optional.of(mockUserRole));
            when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));
            when(roleRepository.findById(55L)).thenReturn(Optional.of(mockRole));
            when(userRoleRepository.deleteAndCount(relationId)).thenReturn(0);

            Result<Void> result = deleteUserRoleService.execute(relationId);

            assertTrue(result.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("User Role could not be deleted or was already removed", result.getMessage().orElse(null));

            verify(userRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Exception Scenarios")
    class ExceptionScenarios {

        @Test
        @DisplayName("Should wrap unexpected exceptions in a RuntimeException")
        void shouldThrowRuntimeExceptionOnUnexpectedError() {

            when(userRoleRepository.findById(relationId)).thenReturn(Optional.of(mockUserRole));
            when(userRepository.findById(10L)).thenReturn(Optional.of(mockUser));
            when(roleRepository.findById(55L)).thenReturn(Optional.of(mockRole));

            IllegalStateException unexpectedError = new IllegalStateException("HikariCP Connection Dead");
            doThrow(unexpectedError).when(userRoleRepository).deleteAndCount(relationId);


            RuntimeException ex = assertThrows(RuntimeException.class, () ->
                    deleteUserRoleService.execute(relationId)
            );

            assertInstanceOf(IllegalStateException.class, ex.getCause());
            assertEquals("HikariCP Connection Dead", ex.getCause().getMessage());
            verify(userRepository, never()).update(any());
        }
    }
}