package com.write.api.application.service.urlTagLink;

import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.mapper.urlTagLink.CreateUrlTagLinkMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
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
class CreateUrlTagLinkServiceTest {

    @Mock
    private CreateUrlTagLinkMapper mapper;

    @Mock
    private IUrlTagLinkRepository repository;

    @InjectMocks
    private CreateUrlTagLinkService service;

    private CreateUrlTagLinkDTO dto;
    private UrlTagLinkModel mappedLink;
    private UrlTagLinkModel savedLink;

    private final Long userId = 1L;
    private final Long urlId = 655L;
    private final Long tagId = 3423L;

    @BeforeEach
    void setup() {
        dto = new CreateUrlTagLinkDTO(
                urlId,
                tagId,
                (short) 1,
                "Any note",
                true
        );

        mappedLink = new UrlTagLinkModel();
        mappedLink.setUrlId(urlId);
        mappedLink.setTagId(tagId);
        mappedLink.setSortOrder((short) 1);
        mappedLink.setNote("Any note");
        mappedLink.setPrimaryTag(true);

        savedLink = new UrlTagLinkModel();
        savedLink.setId(999L);
        savedLink.setUrlId(urlId);
        savedLink.setTagId(tagId);
        savedLink.setSortOrder((short) 1);
        savedLink.setNote("Any note");
        savedLink.setPrimaryTag(true);
        savedLink.setCreatedBy(userId);
        savedLink.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateUrlTagLinkSuccessfully() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class))).thenAnswer(invocation -> {
            UrlTagLinkModel arg = invocation.getArgument(0);
            arg.setId(999L);
            arg.setCreatedAt(LocalDateTime.now());
            return arg;
        });

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(999L);
        assertThat(result.getValue().getUrlId()).isEqualTo(urlId);
        assertThat(result.getValue().getTagId()).isEqualTo(tagId);
        assertThat(result.getValue().getSortOrder()).isEqualTo((short) 1);
        assertThat(result.getValue().getNote()).isEqualTo("Any note");
        assertThat(result.getValue().isPrimaryTag()).isTrue();
        assertThat(result.getValue().getCreatedBy()).isEqualTo(userId);

        ArgumentCaptor<UrlTagLinkModel> captor =
                ArgumentCaptor.forClass(UrlTagLinkModel.class);

        verify(mapper).toModel(dto);
        verify(repository).insert(captor.capture());

        UrlTagLinkModel inserted = captor.getValue();
        assertThat(inserted.getCreatedBy()).isEqualTo(userId);
        assertThat(inserted.getUrlId()).isEqualTo(urlId);
        assertThat(inserted.getTagId()).isEqualTo(tagId);
        assertThat(inserted.getSortOrder()).isEqualTo((short) 1);
        assertThat(inserted.getNote()).isEqualTo("Any note");
        assertThat(inserted.isPrimaryTag()).isTrue();

        InOrder order = inOrder(mapper, repository);
        order.verify(mapper).toModel(dto);
        order.verify(repository).insert(any(UrlTagLinkModel.class));

        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldCreateUrlTagLinkSuccessfullyWithoutOptionalFields() {
        CreateUrlTagLinkDTO dtoWithoutOptional = new CreateUrlTagLinkDTO(
                urlId,
                tagId,
                null,
                null,
                false
        );

        UrlTagLinkModel mappedWithoutOptional = new UrlTagLinkModel();
        mappedWithoutOptional.setUrlId(urlId);
        mappedWithoutOptional.setTagId(tagId);
        mappedWithoutOptional.setPrimaryTag(false);

        when(mapper.toModel(dtoWithoutOptional)).thenReturn(mappedWithoutOptional);
        when(repository.insert(any(UrlTagLinkModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Result<UrlTagLinkModel> result = service.execute(dtoWithoutOptional, userId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getCreatedBy()).isEqualTo(userId);
        assertThat(result.getValue().getSortOrder()).isNull();
        assertThat(result.getValue().getNote()).isNull();
        assertThat(result.getValue().isPrimaryTag()).isFalse();

        ArgumentCaptor<UrlTagLinkModel> captor =
                ArgumentCaptor.forClass(UrlTagLinkModel.class);

        verify(repository).insert(captor.capture());

        UrlTagLinkModel inserted = captor.getValue();
        assertThat(inserted.getCreatedBy()).isEqualTo(userId);
        assertThat(inserted.getUrlId()).isEqualTo(urlId);
        assertThat(inserted.getTagId()).isEqualTo(tagId);
        assertThat(inserted.getSortOrder()).isNull();
        assertThat(inserted.getNote()).isNull();
        assertThat(inserted.isPrimaryTag()).isFalse();

        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn409WhenTagAlreadyPresentInUrl() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_tag_links_unique")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).contains("Tag already present in url");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn404WhenUrlDoesNotExist() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_tag_links_url")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).contains("Url not found");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn404WhenTagDoesNotExist() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_tag_links_tag")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).contains("Tag not found");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn404WhenCreatedByUserDoesNotExist() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_tag_links_created_by")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).contains("User not found");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Database integrity error");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn400WhenNotNullViolationOccurs() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "cannot be null",
                        new RuntimeException("Column 'url_id' cannot be null")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("Required field");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldReturn400WhenDataTooLongOccurs() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "Data too long",
                        new RuntimeException("Data too long for column 'note'")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .contains("exceeded the allowed size");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(mapper.toModel(dto)).thenReturn(mappedLink);
        when(repository.insert(any(UrlTagLinkModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto, userId))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(mapper).toModel(dto);
        verify(repository).insert(any(UrlTagLinkModel.class));
        verifyNoMoreInteractions(mapper, repository);
    }
}