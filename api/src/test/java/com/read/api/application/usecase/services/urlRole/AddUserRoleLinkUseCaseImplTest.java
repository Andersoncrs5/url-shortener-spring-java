package com.read.api.application.usecase.services.urlRole;

import com.read.api.application.usecase.impl.userRole.AddUserRoleLinkUseCaseImpl;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddUserRoleLinkUseCaseImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    AddUserRoleLinkUseCaseImpl useCase;

    UserModel user;
    RoleModel role;

    @BeforeEach
    void setUp() {
        user = new UserModel();
        user.setId(1L);
        user.setName("Anderson");
        user.setEmail("anderson@test.com");
        user.setRoles(new ArrayList<>()); // Lista vazia por padrão

        role = new RoleModel();
        role.setId(2L);
        role.setName("ROLE_ADMIN");
    }

    @Test
    void should_add_role_link_with_success_201() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Result<UserModel> result = useCase.execute(1L, 2L);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(201, result.getStatusCode());
        assertTrue(result.getValue().getRoles().contains("ROLE_ADMIN"));

        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void should_return_success_200_if_user_already_has_the_role() {
        // Arrange
        user.getRoles().add("ROLE_ADMIN"); // Usuário já possui a role

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));

        // Act
        Result<UserModel> result = useCase.execute(1L, 2L);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());
        assertEquals(1, result.getValue().getRoles().size()); // Não duplicou

        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(2L);
        verify(userRepository, never()).save(any()); // Garante que não gastou processamento salvando à toa
    }

    @Test
    void should_return_failure_404_when_user_not_found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Result<UserModel> result = useCase.execute(1L, 2L);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("User not found", result.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void should_return_failure_404_when_role_not_found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        Result<UserModel> result = useCase.execute(1L, 2L);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Role not found", result.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(2L);
        verify(userRepository, never()).save(any());
    }
}