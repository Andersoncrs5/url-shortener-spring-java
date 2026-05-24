package com.write.api.application.service.urlTag;

import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.mapper.urlTag.CreateUrlTagMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.out.repository.IUrlTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUrlTagServiceTest {

    @Mock
    private IUrlTagRepository repository;

    @Mock
    private CreateUrlTagMapper mapper;

    @InjectMocks
    private CreateUrlTagService service;

    private UserModel user;
    private UrlTagModel tag;
    private CreateUrlTagDTO dto;

    @BeforeEach
    void setup() {
        user = new UserModel();
        user.setId(1L);
        user.setName("john");
        user.setEmail("john@test.com");
        user.setPasswordHash("123456");
        user.setActive(true);

        tag = new UrlTagModel();
        tag.setUserId(user.getId());
        tag.setName("anyTag");
        tag.setSlug("any-tag");
        tag.setColor("#000");
        tag.setDescription("backend");
        tag.setActive(true);

        dto = new CreateUrlTagDTO(
                "anyTag",
                "any-tag",
                "#000",
                "backend",
                null,
                true
        );
    }

    @Test
    void shouldCreateUrlTagSuccessfully() {
        when(mapper.toModel(dto)).thenReturn(tag);
        when(repository.insert(any(UrlTagModel.class))).thenReturn(tag);

        Result<UrlTagModel> result =
                service.execute(dto, user.getId());

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();

        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getName()).isEqualTo(dto.name());
        assertThat(result.getValue().getSlug()).isEqualTo(dto.slug());

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagModel.class));
    }

    @Test
    void shouldCreateChildTagSuccessfully() {
        CreateUrlTagDTO childDto = new CreateUrlTagDTO(
                "java",
                "java",
                "#111",
                "java ecosystem",
                null,
                true
        );

        when(mapper.toModel(childDto)).thenReturn(tag);
        when(repository.insert(any())).thenReturn(tag);

        Result<UrlTagModel> result =
                service.execute(childDto, user.getId());

        assertThat(result.isSuccess()).isTrue();

        verify(repository).insert(any());
    }

    @Test
    void shouldReturn404WhenParentTagNotExists() {
        CreateUrlTagDTO childDto = new CreateUrlTagDTO(
                "java",
                "java",
                "#111",
                "java ecosystem",
                99L,
                true
        );

        when(mapper.toModel(childDto)).thenReturn(tag);
        when(repository.existsById(99L)).thenReturn(false);

        Result<UrlTagModel> result =
                service.execute(childDto, user.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Tag not found");

        verify(repository, never()).insert(any());
    }

    @Test
    void shouldReturnConflictWhenSlugAlreadyExists() {
        when(mapper.toModel(dto)).thenReturn(tag);

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_tag_slug")
                );

        when(repository.insert(any()))
                .thenThrow(exception);

        Result<UrlTagModel> result =
                service.execute(dto, user.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .contains("Slug any-tag already exists");
    }

    @Test
    void shouldReturnConflictWhenNameAlreadyExists() {
        when(mapper.toModel(dto)).thenReturn(tag);

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_tag_name")
                );

        when(repository.insert(any()))
                .thenThrow(exception);

        Result<UrlTagModel> result =
                service.execute(dto, user.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .contains("Name anyTag already exists");
    }

    @Test
    void shouldReturn400WhenGenericIntegrityViolationOccurs() {
        when(mapper.toModel(dto)).thenReturn(tag);

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "integrity",
                        new RuntimeException("generic database error")
                );

        when(repository.insert(any()))
                .thenThrow(exception);

        Result<UrlTagModel> result =
                service.execute(dto, user.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("Database integrity error");
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(mapper.toModel(dto)).thenReturn(tag);

        when(repository.insert(any()))
                .thenThrow(new RuntimeException("unexpected error"));

        assertThatThrownBy(() ->
                service.execute(dto, user.getId())
        ).isInstanceOf(InternalServerErrorException.class);
    }
}