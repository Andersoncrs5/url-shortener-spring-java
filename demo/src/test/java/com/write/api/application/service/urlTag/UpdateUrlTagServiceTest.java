package com.write.api.application.service.urlTag;

import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.mapper.urlTag.UpdateUrlTagMapper;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUrlTagServiceTest {

    @Mock
    private UpdateUrlTagMapper mapper;

    @Mock
    private IUrlTagRepository repository;

    @InjectMocks
    private UpdateUrlTagService service;

    private UserModel user;
    private UrlTagModel tag;
    private UpdateUrlTagDTO dto;

    @BeforeEach
    void setup() {
        user = new UserModel();

        user.setId(1L);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setPasswordHash("123");
        user.setActive(true);

        tag = new UrlTagModel();

        tag.setId(10L);
        tag.setUserId(user.getId());
        tag.setName("Backend");
        tag.setSlug("backend");
        tag.setActive(true);

        dto = new UpdateUrlTagDTO(
                "Java",
                "java",
                "#000000",
                "Java ecosystem",
                null,
                true
        );
    }

    @Test
    void shouldUpdateUrlTagSuccessfully() {

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        when(repository.save(tag))
                .thenReturn(tag);

        Result<UrlTagModel> result =
                service.execute(10L, dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isEqualTo(tag);

        verify(repository).findById(10L);
        verify(mapper).updateModelFromDto(dto, tag);
        verify(repository).save(tag);
    }

    @Test
    void shouldReturn404WhenUrlTagNotFound() {

        when(repository.findById(10L))
                .thenReturn(Optional.empty());

        Result<UrlTagModel> result =
                service.execute(10L, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Tag not found");

        verify(repository).findById(10L);

        verify(repository, never()).save(any());
        verify(mapper, never())
                .updateModelFromDto(any(), any());
    }

    @Test
    void shouldReturn409WhenParentIdIsSameAsTagId() {

        UpdateUrlTagDTO dtoConflict =
                new UpdateUrlTagDTO(
                        "Java",
                        "java",
                        "#000000",
                        "Java ecosystem",
                        10L,
                        true
                );

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        Result<UrlTagModel> result =
                service.execute(10L, dtoConflict);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Parent Id tag conflict");

        verify(repository).findById(10L);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturn404WhenParentTagNotFound() {

        UpdateUrlTagDTO dtoWithParent =
                new UpdateUrlTagDTO(
                        "Java",
                        "java",
                        "#000000",
                        "Java ecosystem",
                        99L,
                        true
                );

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        when(repository.existsById(99L))
                .thenReturn(false);

        Result<UrlTagModel> result =
                service.execute(10L, dtoWithParent);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Parent Url Tag not found");

        verify(repository).existsById(99L);

        verify(repository, never()).save(any());
    }

    @Test
    void shouldHandleDuplicateSlugException() {

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        when(repository.save(tag))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "error",
                                new RuntimeException(
                                        "uk_url_tag_slug"
                                )
                        )
                );

        Result<UrlTagModel> result =
                service.execute(10L, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Slug java already exists");
    }

    @Test
    void shouldHandleDuplicateNameException() {

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        when(repository.save(tag))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "error",
                                new RuntimeException(
                                        "uk_url_tag_name"
                                )
                        )
                );

        Result<UrlTagModel> result =
                service.execute(10L, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .isEqualTo("Name Java already exists");
    }

    @Test
    void shouldHandleGenericDatabaseError() {

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        when(repository.save(tag))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "error",
                                new RuntimeException(
                                        "generic sql error"
                                )
                        )
                );

        Result<UrlTagModel> result =
                service.execute(10L, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("Database integrity error");
    }

    @Test
    void shouldThrowInternalServerErrorException() {

        when(repository.findById(10L))
                .thenReturn(Optional.of(tag));

        when(repository.save(tag))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() ->
                service.execute(10L, dto)
        ).isInstanceOf(InternalServerErrorException.class);
    }
}
