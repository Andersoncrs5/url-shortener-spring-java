package com.write.api.application.service.urlTagLink;

import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.mapper.urlTagLink.UpdateUrlTagLinkMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.core.domain.exception.InternalServerErrorException;
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
class UpdateUrlTagLinkServiceTest {

    @Mock
    private UpdateUrlTagLinkMapper mapper;

    @Mock
    private IUrlTagLinkRepository repository;

    @InjectMocks
    private UpdateUrlTagLinkService service;

    private UrlTagLinkModel link;
    private UpdateUrlTagLinkDTO dto;

    private final Long id = 1L;

    @BeforeEach
    void setup() {
        link = new UrlTagLinkModel();
        link.setId(id);
        link.setUrlId(10L);
        link.setTagId(20L);
        link.setSortOrder((short) 1);
        link.setNote("Old note");
        link.setPrimaryTag(false);
        link.setCreatedBy(100L);
        link.setCreatedAt(LocalDateTime.now().minusDays(1));
        link.setUpdatedAt(LocalDateTime.now().minusHours(1));

        dto = new UpdateUrlTagLinkDTO(
                (short) 5,
                "Updated note",
                true
        );
    }

    @Test
    void shouldUpdateUrlTagLinkSuccessfully() {
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);

            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());

            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        when(repository.save(any(UrlTagLinkModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isFailure()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();

        UrlTagLinkModel saved = result.getValue();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getUrlId()).isEqualTo(link.getUrlId());
        assertThat(saved.getTagId()).isEqualTo(link.getTagId());
        assertThat(saved.getSortOrder()).isEqualTo((short) 5);
        assertThat(saved.getNote()).isEqualTo("Updated note");
        assertThat(saved.isPrimaryTag()).isTrue();
        assertThat(saved.getCreatedBy()).isEqualTo(link.getCreatedBy());

        ArgumentCaptor<UrlTagLinkModel> captor =
                ArgumentCaptor.forClass(UrlTagLinkModel.class);

        verify(repository).findById(id);
        verify(mapper).update(dto, link);
        verify(repository).save(captor.capture());

        UrlTagLinkModel savedArg = captor.getValue();
        assertThat(savedArg.getId()).isEqualTo(id);
        assertThat(savedArg.getSortOrder()).isEqualTo((short) 5);
        assertThat(savedArg.getNote()).isEqualTo("Updated note");
        assertThat(savedArg.isPrimaryTag()).isTrue();

        InOrder order = inOrder(repository, mapper);
        order.verify(repository).findById(id);
        order.verify(mapper).update(dto, link);
        order.verify(repository).save(link);

        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void shouldReturn404WhenUrlTagLinkNotFound() {
        when(repository.findById(id)).thenReturn(java.util.Optional.empty());

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url Tag Link not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturn409WhenTagAlreadyPresentInUrl() {
        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);
            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());
            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        when(repository.save(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate",
                        new RuntimeException("uk_url_tag_links_unique")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage()).isEqualTo("Tag already present in url");

        verify(repository).findById(id);
        verify(mapper).update(dto, link);
        verify(repository).save(link);
    }

    @Test
    void shouldReturn404WhenUrlForeignKeyFails() {
        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);
            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());
            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        when(repository.save(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_tag_links_url")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url not found");
    }

    @Test
    void shouldReturn404WhenTagForeignKeyFails() {
        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);
            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());
            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        when(repository.save(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_tag_links_tag")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Tag not found");
    }

    @Test
    void shouldReturn404WhenCreatedByForeignKeyFails() {
        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);
            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());
            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        when(repository.save(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_url_tag_links_created_by")
                ));

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("User not found");
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);
            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());
            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.save(any(UrlTagLinkModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<UrlTagLinkModel> result = service.execute(dto, id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Database integrity error");
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(repository.findById(id)).thenReturn(java.util.Optional.of(link));
        doAnswer(invocation -> {
            UpdateUrlTagLinkDTO source = invocation.getArgument(0);
            UrlTagLinkModel target = invocation.getArgument(1);
            target.setSortOrder(source.sortOrder());
            target.setNote(source.note());
            target.setPrimaryTag(source.primaryTag() != null && source.primaryTag());
            return null;
        }).when(mapper).update(eq(dto), any(UrlTagLinkModel.class));

        when(repository.save(any(UrlTagLinkModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto, id))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(repository).findById(id);
        verify(mapper).update(dto, link);
        verify(repository).save(link);
    }
}