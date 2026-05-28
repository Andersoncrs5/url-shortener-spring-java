package com.write.api.application.service.role;

import com.write.api.application.dto.role.UpdateRoleDTO;
import com.write.api.application.mapper.role.UpdateRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.out.repository.IRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRoleServiceTest {

    @Mock
    private IRoleRepository repository;

    @Mock
    private UpdateRoleMapper mapper;

    @InjectMocks
    private UpdateRoleService service;

    private RoleModel role;
    private UpdateRoleDTO dto;

    private final Long id = 1L;

    @BeforeEach
    void setup() {
        role = new RoleModel();
        role.setId(id);
        role.setName("USER");
        role.setDescription("Default role");
        role.setActive(true);

        dto = new UpdateRoleDTO(
                "ADMIN",
                "Administrator role",
                true
        );
    }

    @Test
    void shouldUpdateRoleSuccessfully() {
        when(repository.findById(id))
                .thenReturn(Optional.of(role));

        doAnswer(invocation -> {
            UpdateRoleDTO source = invocation.getArgument(0);
            RoleModel target = invocation.getArgument(1);

            target.setName(source.name());
            target.setDescription(source.description());

            target.setActive(source.active());

            return null;
        }).when(mapper).merge(eq(dto), any(RoleModel.class));

        when(repository.save(any(RoleModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<RoleModel> result = service.execute(id, dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        RoleModel saved = result.getValue();

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getName()).isEqualTo("ADMIN");
        assertThat(saved.getDescription()).isEqualTo("Administrator role");
        assertThat(saved.isActive()).isTrue();

        verify(repository).findById(id);
        verify(mapper).merge(dto, role);
        verify(repository).save(role);

        InOrder order = inOrder(repository, mapper);

        order.verify(repository).findById(id);
        order.verify(mapper).merge(dto, role);
        order.verify(repository).save(role);

        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldReturn404WhenRoleNotFound() {
        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Result<RoleModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Role not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(id);

        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturn409WhenRoleNameAlreadyExists() {
        when(repository.findById(id))
                .thenReturn(Optional.of(role));

        doNothing()
                .when(mapper)
                .merge(dto, role);

        when(repository.save(role))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "duplicate",
                                new RuntimeException("uk_roles_name")
                        )
                );

        Result<RoleModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Name: 'ADMIN' already exists");

        verify(repository).findById(id);
        verify(mapper).merge(dto, role);
        verify(repository).save(role);
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(repository.findById(id))
                .thenReturn(Optional.of(role));

        doNothing()
                .when(mapper)
                .merge(dto, role);

        RuntimeException root = mock(RuntimeException.class);

        when(root.getMessage()).thenReturn(null);

        when(repository.save(role))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "integrity",
                                root
                        )
                );

        Result<RoleModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .isEqualTo("Database integrity error");
    }

    @Test
    void shouldReturn400WhenDataTooLongOccurs() {
        when(repository.findById(id))
                .thenReturn(Optional.of(role));

        doNothing()
                .when(mapper)
                .merge(dto, role);

        when(repository.save(role))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "too long",
                                new RuntimeException(
                                        "Data too long for column 'name'"
                                )
                        )
                );

        Result<RoleModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("exceeded the allowed size");
    }

    @Test
    void shouldReturn400WhenNotNullViolationOccurs() {
        when(repository.findById(id))
                .thenReturn(Optional.of(role));

        doNothing()
                .when(mapper)
                .merge(dto, role);

        when(repository.save(role))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "null",
                                new RuntimeException(
                                        "Column 'name' cannot be null"
                                )
                        )
                );

        Result<RoleModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("Required field");
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(repository.findById(id))
                .thenReturn(Optional.of(role));

        doNothing()
                .when(mapper)
                .merge(dto, role);

        when(repository.save(role))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(id, dto))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(repository).findById(id);
        verify(mapper).merge(dto, role);
        verify(repository).save(role);
    }
}