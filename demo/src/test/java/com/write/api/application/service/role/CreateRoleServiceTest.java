package com.write.api.application.service.role;

import com.write.api.application.dto.role.CreateRoleDTO;
import com.write.api.application.mapper.role.CreateRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.out.repository.IRoleRepository;
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
class CreateRoleServiceTest {

    @Mock
    private IRoleRepository repository;

    @Mock
    private CreateRoleMapper mapper;

    @InjectMocks
    private CreateRoleService service;

    private CreateRoleDTO dto;
    private RoleModel mappedRole;

    @BeforeEach
    void setup() {
        dto = new CreateRoleDTO(
                "ROLE_ADMIN",
                "System administrator",
                true
        );

        mappedRole = new RoleModel();
        mappedRole.setName(dto.name());
        mappedRole.setDescription(dto.description());
        mappedRole.setActive(dto.active());
    }

    @Test
    void shouldCreateRoleSuccessfully() {
        when(mapper.toModel(dto)).thenReturn(mappedRole);

        when(repository.insert(any(RoleModel.class)))
                .thenAnswer(invocation -> {
                    RoleModel arg = invocation.getArgument(0);
                    arg.setId(1L);
                    arg.setCreatedAt(LocalDateTime.now());
                    arg.setUpdatedAt(LocalDateTime.now());
                    return arg;
                });

        Result<RoleModel> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();

        RoleModel value = result.getValue();
        assertThat(value.getId()).isEqualTo(1L);
        assertThat(value.getName()).isEqualTo("ROLE_ADMIN");
        assertThat(value.getDescription()).isEqualTo("System administrator");
        assertThat(value.isActive()).isTrue();
        assertThat(value.getCreatedAt()).isNotNull();
        assertThat(value.getUpdatedAt()).isNotNull();

        ArgumentCaptor<RoleModel> captor = ArgumentCaptor.forClass(RoleModel.class);

        verify(mapper).toModel(dto);
        verify(repository).insert(captor.capture());

        RoleModel inserted = captor.getValue();
        assertThat(inserted.getName()).isEqualTo(dto.name());
        assertThat(inserted.getDescription()).isEqualTo(dto.description());
        assertThat(inserted.isActive()).isTrue();

        InOrder order = inOrder(mapper, repository);
        order.verify(mapper).toModel(dto);
        order.verify(repository).insert(any(RoleModel.class));

        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn409WhenRoleNameAlreadyExists() {
        when(mapper.toModel(dto)).thenReturn(mappedRole);

        when(repository.insert(any(RoleModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key",
                        new RuntimeException("uk_roles_name")
                ));

        Result<RoleModel> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Name: 'ROLE_ADMIN' already exists");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(RoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(mapper.toModel(dto)).thenReturn(mappedRole);

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any(RoleModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<RoleModel> result = service.execute(dto);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Database integrity error");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(RoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(mapper.toModel(dto)).thenReturn(mappedRole);

        when(repository.insert(any(RoleModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(RoleModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }
}