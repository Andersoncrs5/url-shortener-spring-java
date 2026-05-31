package com.write.api.application.service.userRole;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.ports.out.repository.IRoleRepository;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserRoleServiceTest {

    @Mock
    private IUserRoleRepository repository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private DeleteUserRoleService service;

    private final Long linkId = 1L;
    private final Long performedByUserId = 10L;
    private final Long targetUserId = 20L;
    private final Long roleId = 30L;

    private UserRoleModel link;
    private UserModel performedBy;
    private UserModel targetUser;
    private RoleModel userRole;
    private RoleModel adminRole;
    private RoleModel superAdminRole;

    @BeforeEach
    void setup() {
        link = new UserRoleModel();
        link.setId(linkId);
        link.setUserId(targetUserId);
        link.setRoleId(roleId);
        link.setAssignedByUserId(performedByUserId);

        performedBy = buildUser(performedByUserId, "John Performer");
        targetUser = buildUser(targetUserId, "Target User");

        userRole = buildRole("USER");
        adminRole = buildRole("ADMIN");
        superAdminRole = buildRole("SUPER_ADMIN");
    }

    @Test
    void shouldDeleteUserRoleSuccessfully() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(userRole));
        when(userRepository.findById(performedByUserId)).thenReturn(Optional.of(performedBy));
        when(repository.deleteById(linkId)).thenReturn(1);

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNull();

        InOrder order = inOrder(repository, userRepository, roleRepository);
        order.verify(repository).findById(linkId);
        order.verify(userRepository).findById(targetUserId);
        order.verify(roleRepository).findById(roleId);
        order.verify(userRepository).findById(performedByUserId);
        order.verify(repository).deleteById(linkId);

        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldReturn404WhenUserRoleLinkNotFound() {
        when(repository.findById(linkId)).thenReturn(Optional.empty());

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User Role not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(linkId);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(roleRepository);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturn404WhenTargetUserNotFound() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User not found");

        verify(repository).findById(linkId);
        verify(userRepository).findById(targetUserId);
        verifyNoInteractions(roleRepository);
        verifyNoMoreInteractions(repository, userRepository);
    }

    @Test
    void shouldReturn404WhenRoleNotFound() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Role not found");

        verify(repository).findById(linkId);
        verify(userRepository).findById(targetUserId);
        verify(roleRepository).findById(roleId);
        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldReturn404WhenPerformedByUserNotFound() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(userRole));
        when(userRepository.findById(performedByUserId)).thenReturn(Optional.empty());

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Assigned user not found");

        verify(repository).findById(linkId);
        verify(userRepository).findById(targetUserId);
        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(performedByUserId);
        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldReturn409WhenTryingToRemoveSuperAdminRole() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(superAdminRole));
        when(userRepository.findById(performedByUserId)).thenReturn(Optional.of(performedBy));

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).contains("SUPER_ADMIN");

        verify(repository).findById(linkId);
        verify(userRepository).findById(targetUserId);
        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(performedByUserId);
        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldReturn409WhenNonSuperAdminTriesToRemoveAdminRole() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(performedByUserId)).thenReturn(Optional.of(performedBy));

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .containsIgnoringCase("Super Adm");

        verify(repository).findById(linkId);
        verify(userRepository).findById(targetUserId);
        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(performedByUserId);
        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldAllowSuperAdminToRemoveAdminRole() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(adminRole));
        when(userRepository.findById(performedByUserId)).thenReturn(Optional.of(buildUserWithRoles(performedByUserId, "SUPER_ADMIN")));
        when(repository.deleteById(linkId)).thenReturn(1);

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        verify(repository).findById(linkId);
        verify(userRepository).findById(targetUserId);
        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(performedByUserId);
        verify(repository).deleteById(linkId);
        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldReturn500WhenDeleteAffectsZeroRowsAfterPassingChecks() {
        when(repository.findById(linkId)).thenReturn(Optional.of(link));
        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(userRole));
        when(userRepository.findById(performedByUserId)).thenReturn(Optional.of(performedBy));
        when(repository.deleteById(linkId)).thenReturn(0);

        Result<Void> result = service.deleteById(linkId, performedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(500);
        assertThat(result.getMessage()).containsIgnoringCase("failed");

        verify(repository).deleteById(linkId);
        verifyNoMoreInteractions(repository, userRepository, roleRepository);
    }

    @Test
    void shouldPropagateUnexpectedException() {
        when(repository.findById(linkId)).thenThrow(new RuntimeException("db error"));

        assertThatThrownBy(() -> service.deleteById(linkId, performedByUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("db error");

        verify(repository).findById(linkId);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(roleRepository);
        verifyNoMoreInteractions(repository);
    }

    private RoleModel buildRole(String name) {
        RoleModel role = new RoleModel();
        role.setId(roleId);
        role.setName(name);
        role.setDescription("Role " + name);
        role.setActive(true);
        return role;
    }

    private UserModel buildUser(Long id, String name) {
        UserModel user = new UserModel();
        user.setId(id);
        user.setName(name);
        user.setActive(true);
        setRoles(user, null);
        return user;
    }

    private UserModel buildUserWithRoles(Long id, String... roles) {
        UserModel user = new UserModel();
        user.setId(id);
        user.setName("Performer");
        user.setActive(true);
        setRoles(user, roles == null ? null : List.of(roles));
        return user;
    }

    private void setRoles(UserModel user, Collection<String> roles) {
        try {
            Field field = UserModel.class.getDeclaredField("roles");
            field.setAccessible(true);

            if (roles == null) {
                field.set(user, null);
                return;
            }

            Class<?> type = field.getType();
            if (List.class.isAssignableFrom(type)) {
                field.set(user, List.copyOf(roles));
            } else if (Set.class.isAssignableFrom(type)) {
                field.set(user, Set.copyOf(roles));
            } else if (Collection.class.isAssignableFrom(type)) {
                field.set(user, List.copyOf(roles));
            } else {
                field.set(user, null);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // Ajuste aqui se o seu UserModel usar outro nome/tipo de campo.
        }
    }
}