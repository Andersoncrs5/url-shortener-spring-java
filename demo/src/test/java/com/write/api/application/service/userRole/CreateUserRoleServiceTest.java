package com.write.api.application.service.userRole;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.mapper.userRole.CreateUserRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserRoleModel;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserRoleServiceTest {

    @Mock
    private IUserRoleRepository repository;

    @Mock
    private CreateUserRoleMapper mapper;

    @InjectMocks
    private CreateUserRoleService service;

    private CreateUserRoleDTO dto;
    private UserRoleModel mappedModel;

    private final Long assignedByUserId = 10L;

    @BeforeEach
    void setup() {
        dto = new CreateUserRoleDTO(
                1L,
                2L
        );

        mappedModel = new UserRoleModel();
        mappedModel.setUserId(dto.userId());
        mappedModel.setRoleId(dto.roleId());
    }

    @Test
    void shouldCreateUserRoleSuccessfully() {
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

        verify(mapper).toModel(dto);
        verify(repository).insert(captor.capture());

        UserRoleModel inserted = captor.getValue();
        assertThat(inserted.getUserId()).isEqualTo(dto.userId());
        assertThat(inserted.getRoleId()).isEqualTo(dto.roleId());
        assertThat(inserted.getAssignedByUserId()).isEqualTo(assignedByUserId);

        InOrder order = inOrder(mapper, repository);
        order.verify(mapper).toModel(dto);
        order.verify(repository).insert(any(UserRoleModel.class));

        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn409WhenUserAlreadyHasThisRole() {
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

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn404WhenUserNotFound() {
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_user_roles_user_id")
                ));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User not found");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn404WhenRoleNotFound() {
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_user_roles_role_id")
                ));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Role not found");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<UserRoleModel> result = service.execute(dto, assignedByUserId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Database integrity error");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenUnexpectedExceptionOccurs() {
        when(mapper.toModel(dto)).thenReturn(mappedModel);

        when(repository.insert(any(UserRoleModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto, assignedByUserId))
                .isInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("unexpected");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UserRoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }
}