package com.write.api.application.service.url;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.mapper.url.UpdateUrlMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUrlServiceTest {

    @Mock
    private IUrlRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @Mock
    private UpdateUrlMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateUrlService service;

    private UrlModel url;
    private UpdateUrlDTO dto;

    private final Long id = 1L;

    @BeforeEach
    void setup() {
        url = new UrlModel();
        url.setId(id);
        url.setVersion(1L);
        url.setUserId(10L);
        url.setShortCode("abc123");
        url.setDescription("Old description");
        url.setFaviconUrl("https://old.com/favicon.ico");
        url.setOriginalUrl("https://old.com");
        url.setTitle("Old title");
        url.setDomain("old.com");
        url.setStatus(UrlStatusEnum.ACTIVE);
        url.setAccessType(UrlAccessTypeEnum.PUBLIC);
        url.setPasswordHash("old-password");
        url.setCustomAlias(false);
        url.setDeletedAt(null);
        url.setExpiresAt(LocalDateTime.now().plusDays(7));
        url.setLastAccessAt(null);
        url.setCreatedAt(LocalDateTime.now().minusDays(1));
        url.setUpdatedAt(LocalDateTime.now().minusHours(1));

        dto = new UpdateUrlDTO(
                "https://new.com/favicon.ico",
                "New title",
                "New description",
                "https://new.com/favicon.ico",
                "new.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PRIVATE,
                "12345678",
                LocalDateTime.now().plusDays(10)
                );
    }

    @Test
    void shouldUpdateUrlSuccessfullyWithPassword() {

        doAnswer(invocation -> {
            UpdateUrlDTO source = invocation.getArgument(0);
            UrlModel target = invocation.getArgument(1);

            target.setOriginalUrl(source.originalUrl());
            target.setTitle(source.title());
            target.setDescription(source.description());
            target.setFaviconUrl(source.faviconUrl());
            target.setDomain(source.domain());
            target.setStatus(source.status());
            target.setAccessType(source.accessType());
            target.setExpiresAt(source.expiresAt());

            return null;
        }).when(mapper).update(any(), any());

        when(repository.findById(id))
                .thenReturn(Optional.of(url));

        when(passwordEncoder.encode(dto.password()))
                .thenReturn("encoded-password");

        when(repository.save(any(UrlModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isNotNull();

        ArgumentCaptor<UrlModel> captor =
                ArgumentCaptor.forClass(UrlModel.class);

        verify(repository).save(captor.capture());

        UrlModel saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getUserId()).isEqualTo(url.getUserId());
        assertThat(saved.getShortCode()).isEqualTo(url.getShortCode());

        assertThat(saved.getOriginalUrl())
                .isEqualTo(dto.originalUrl());

        assertThat(saved.getTitle())
                .isEqualTo(dto.title());

        assertThat(saved.getDescription())
                .isEqualTo(dto.description());

        assertThat(saved.getFaviconUrl())
                .isEqualTo(dto.faviconUrl());

        assertThat(saved.getDomain())
                .isEqualTo(dto.domain());

        assertThat(saved.getStatus())
                .isEqualTo(dto.status());

        assertThat(saved.getAccessType())
                .isEqualTo(dto.accessType());

        assertThat(saved.getExpiresAt())
                .isEqualTo(dto.expiresAt());

        assertThat(saved.getPasswordHash())
                .isEqualTo("encoded-password");

        InOrder order = inOrder(repository, mapper, passwordEncoder);

        order.verify(repository).findById(id);
        order.verify(mapper).update(dto, url);
        order.verify(passwordEncoder).encode(dto.password());
        order.verify(repository).save(url);

        verifyNoMoreInteractions(repository, mapper, passwordEncoder);
    }

    @Test
    void shouldUpdateUrlSuccessfullyWithoutPassword() {
        UpdateUrlDTO dtoWithoutPassword = new UpdateUrlDTO(
                "https://new.com/favicon.ico",
                "New title",
                "New description",
                "https://new.com/favicon.ico",
                "new.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PRIVATE,
                null,
                LocalDateTime.now().plusDays(10)
        );

        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(repository.save(any(UrlModel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(outbox.execute(any(CreateOutboxEventCommand.class))).thenReturn(Result.success(null, 201));

        Result<UrlModel> result = service.execute(id, dtoWithoutPassword);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);

        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).save(captor.capture());

        UrlModel saved = captor.getValue();
        assertThat(saved.getPasswordHash()).isEqualTo("old-password");

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturn404WhenUrlNotFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("Url not found");
        assertThat(result.getValue()).isNull();

        verify(repository).findById(id);
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper, passwordEncoder);
    }

    @Test
    void shouldReturn409WhenShortCodeAlreadyExists() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(repository.save(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key",
                        new RuntimeException("uk_urls_short_code")
                ));

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .contains("Short code")
                .contains(url.getShortCode());

        verify(repository).findById(id);
        verify(mapper).update(dto, url);
        verify(passwordEncoder).encode(dto.password());
        verify(repository).save(any(UrlModel.class));
    }

    @Test
    void shouldReturn404WhenUserForeignKeyDoesNotExist() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(repository.save(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_urls_user")
                ));

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).containsIgnoringCase("User not found");

        verify(repository).save(any(UrlModel.class));
    }

    @Test
    void shouldReturn400WhenRequiredFieldIsMissing() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(repository.save(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "cannot be null",
                        new RuntimeException("Column 'title' cannot be null")
                ));

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .containsIgnoringCase("Required field is missing");
    }

    @Test
    void shouldReturn400WhenDataTooLongOccurs() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(repository.save(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "too long",
                        new RuntimeException("Data too long for column 'title'")
                ));

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .containsIgnoringCase("exceeded the allowed size");
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.save(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException("integrity", root));

        Result<UrlModel> result = service.execute(id, dto);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Database integrity error");
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(repository.findById(id)).thenReturn(Optional.of(url));
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(repository.save(any(UrlModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(id, dto))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(repository).findById(id);
        verify(mapper).update(dto, url);
        verify(passwordEncoder).encode(dto.password());
        verify(repository).save(any(UrlModel.class));
    }
}