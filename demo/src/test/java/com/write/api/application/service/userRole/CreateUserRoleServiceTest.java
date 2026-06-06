package com.write.api.application.service.userRole;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.mapper.userRole.CreateUserRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.ports.out.repository.IRoleRepository;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserRoleServiceTest {

    @Mock
    private IUserRoleRepository repository;

    @Mock
    private CreateUserRoleMapper mapper;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private CreateUserRoleService service;

    private CreateUserRoleDTO dto;
    private UserRoleModel mappedModel;

    private final Long assignedByUserId = 10L;
    private final Long targetUserId = 20L;
    private final Long roleId = 30L;

    @BeforeEach
    void setup() {
        dto = new CreateUserRoleDTO(
                targetUserId,
                roleId
        );

        mappedModel = new UserRoleModel();
        mappedModel.setUserId(dto.userId());
        mappedModel.setRoleId(dto.roleId());
    }

    @Test
    void shouldCreateUserRoleSuccessfully() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenAnswer(invocation -> {
                    UserRoleModel arg = invocation.getArgument(0);
                    arg.setId(999L);
                    arg.setCreatedAt(LocalDateTime.now());
                    arg.setUpdatedAt(LocalDateTime.now());
                    return arg;
                });

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();

        UserRoleModel value = result.getValue();
        assertThat(value.getId()).isEqualTo(999L);
        assertThat(value.getUserId()).isEqualTo(dto.userId());
        assertThat(value.getRoleId()).isEqualTo(dto.roleId());
        assertThat(value.getAssignedByUserId()).isEqualTo(assignedByUserId);
        assertThat(value.getCreatedAt()).isNotNull();
        assertThat(value.getUpdatedAt()).isNotNull();

        ArgumentCaptor<UserRoleModel> captor =
                ArgumentCaptor.forClass(UserRoleModel.class);

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(captor.capture());

        UserRoleModel inserted = captor.getValue();
        assertThat(inserted.getUserId()).isEqualTo(dto.userId());
        assertThat(inserted.getRoleId()).isEqualTo(dto.roleId());
        assertThat(inserted.getAssignedByUserId()).isEqualTo(assignedByUserId);

        InOrder order = inOrder(roleRepository, userRepository, mapper, repository);
        order.verify(roleRepository).findById(roleId);
        order.verify(userRepository).findById(assignedByUserId);
        order.verify(mapper).toModel(dto);
        order.verify(repository).insert(any(UserRoleModel.class));


    }

    @Test
    void shouldReturn404WhenRoleNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Role not found");
        assertThat(result.getValue()).isNull();

        verify(roleRepository).findById(roleId);
        verifyNoInteractions(userRepository, mapper, repository);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void shouldReturn404WhenAssignedByUserNotFound() {
        RoleModel role = buildRole("USER");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.empty());

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Assigned user not found");
        assertThat(result.getValue()).isNull();

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verifyNoInteractions(mapper, repository);
        verifyNoMoreInteractions(roleRepository, userRepository);
    }

    @Test
    void shouldReturn409WhenRoleIsSuperAdmin() {
        RoleModel role = buildRole("SUPER_ADMIN");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).isEqualTo("Just one Super Adm");
        assertThat(result.getValue()).isNull();

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verifyNoMoreInteractions(roleRepository, userRepository);
    }

    @Test
    void shouldReturn403WhenAdminRoleIsGrantedByNonSuperAdmin() {
        RoleModel role = buildRole("ADMIN");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(repository.findRoleByUserId(assignedByUserId)).thenReturn(List.of("USER"));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).isEqualTo("Just one Super Adm can add role admin");
        assertThat(result.getValue()).isNull();

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(repository).findRoleByUserId(assignedByUserId);

        verifyNoInteractions(mapper);
        verifyNoMoreInteractions(roleRepository, userRepository, repository);
    }

    @Test
    void shouldAllowAdminRoleWhenGrantedBySuperAdmin() {
        RoleModel role = buildRole("ADMIN");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(repository.findRoleByUserId(assignedByUserId)).thenReturn(List.of("SUPER_ADMIN"));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenAnswer(invocation -> {
                    UserRoleModel arg = invocation.getArgument(0);
                    arg.setId(999L);
                    arg.setCreatedAt(LocalDateTime.now());
                    arg.setUpdatedAt(LocalDateTime.now());
                    return arg;
                });

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(repository).findRoleByUserId(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));


    }

    @Test
    void shouldReturn409WhenUserAlreadyHasThisRole() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_user_roles_user_role")
                ));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).isEqualTo("User already has this role");

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));

    }

    @Test
    void shouldReturn404WhenUserFkFails() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_user_roles_user_id")
                ));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User not found");

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));

    }

    @Test
    void shouldReturn404WhenAssignedByFkFails() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_user_roles_assigned_by_user_id")
                ));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Assigned user not found");

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));

    }

    @Test
    void shouldReturn404WhenRoleFkFails() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_user_roles_role_id")
                ));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Role not found");

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));

    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));

    }

    @Test
    void shouldThrowRuntimeExceptionWhenUnexpectedExceptionOccurs() {
        RoleModel role = buildRole("USER");
        UserModel assignedBy = buildUser();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.findById(assignedByUserId)).thenReturn(Optional.of(assignedBy));
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto, assignedByUserId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("unexpected");

        verify(roleRepository).findById(roleId);
        verify(userRepository).findById(assignedByUserId);
        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));
    }

    private RoleModel buildRole(String name) {
        RoleModel role = new RoleModel();
        role.setId(roleId);
        role.setName(name);
        role.setDescription("Role " + name);
        role.setActive(true);
        return role;
    }

    private UserModel buildUser() {
        UserModel user = new UserModel();
        user.setId(assignedByUserId);
        return user;
    }
}