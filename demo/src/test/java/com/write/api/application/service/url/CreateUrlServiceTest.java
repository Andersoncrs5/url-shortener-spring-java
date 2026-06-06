package com.write.api.application.service.url;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.url.UrlCreatedEvent;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.mapper.url.CreateUrlMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
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
import java.util.UUID;

import static com.write.api.shared.utils.Base62.encode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUrlServiceTest {

    @Mock
    private CreateUrlMapper mapper;

    @Mock
    private IUrlRepository repository;

    @Mock
    private CreateOutboxEventUseCase outbox;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SnowflakeIdGenerator idGen;

    @InjectMocks
    private CreateUrlService service;

    private CreateUrlDTO dto;
    private UrlModel mappedUrl;

    private final Long userId = 1L;
    private final Long generatedId = 123445234L;
    private final String expectedShortCode = encode(generatedId);

    @BeforeEach
    void setup() {
        dto = new CreateUrlDTO(
                "https://example.com/article/" + UUID.randomUUID(),
                "My title",
                "Any desc",
                "https://example.com/favicon.ico",
                "example.com",
                UrlAccessTypeEnum.PUBLIC,
                "12345678",
                LocalDateTime.now().plusDays(7)
        );

        mappedUrl = new UrlModel();
        mappedUrl.setDescription(dto.description());
        mappedUrl.setFaviconUrl(dto.faviconUrl());
        mappedUrl.setOriginalUrl(dto.originalUrl());
        mappedUrl.setTitle(dto.title());
        mappedUrl.setDomain(dto.domain());
        mappedUrl.setStatus(UrlStatusEnum.ACTIVE);
        mappedUrl.setAccessType(dto.accessType());
        mappedUrl.setCustomAlias(false);
    }

    @Test
    void shouldCreateUrlSuccessfullyWithPassword() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class))).thenAnswer(invocation -> {
            UrlModel arg = invocation.getArgument(0);
            arg.setId(generatedId);
            arg.setVersion(1L);
            arg.setShortCode(expectedShortCode);
            arg.setCreatedAt(LocalDateTime.now());
            arg.setUpdatedAt(LocalDateTime.now());
            return arg;
        });

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();

        UrlModel value = result.getValue();
        assertThat(value.getId()).isEqualTo(generatedId);
        assertThat(value.getUserId()).isEqualTo(userId);
        assertThat(value.getShortCode()).isEqualTo(expectedShortCode);
        assertThat(value.getDescription()).isEqualTo(dto.description());
        assertThat(value.getFaviconUrl()).isEqualTo(dto.faviconUrl());
        assertThat(value.getOriginalUrl()).isEqualTo(dto.originalUrl());
        assertThat(value.getTitle()).isEqualTo(dto.title());
        assertThat(value.getDomain()).isEqualTo(dto.domain());
        assertThat(value.getStatus()).isEqualTo(UrlStatusEnum.ACTIVE);
        assertThat(value.getAccessType()).isEqualTo(UrlAccessTypeEnum.PASSWORD_PROTECTED);
        assertThat(value.getPasswordHash()).isEqualTo("encoded-password");

        ArgumentCaptor<UrlModel> urlCaptor = ArgumentCaptor.forClass(UrlModel.class);
        ArgumentCaptor<CreateOutboxEventCommand> outboxCaptor =
                ArgumentCaptor.forClass(CreateOutboxEventCommand.class);

        InOrder order = inOrder(idGen, mapper, passwordEncoder, repository, outbox);
        order.verify(idGen).nextId();
        order.verify(mapper).toModel(dto);
        order.verify(passwordEncoder).encode(dto.password());
        order.verify(repository).insert(urlCaptor.capture());
        order.verify(outbox).execute(outboxCaptor.capture());

        UrlModel inserted = urlCaptor.getValue();
        assertThat(inserted.getUserId()).isEqualTo(userId);
        assertThat(inserted.getShortCode()).isEqualTo(expectedShortCode);
        assertThat(inserted.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(inserted.getAccessType()).isEqualTo(UrlAccessTypeEnum.PASSWORD_PROTECTED);

        CreateOutboxEventCommand command = outboxCaptor.getValue();
        assertThat(command.aggregateType()).isEqualTo(AggregateTypeEnum.URL);
        assertThat(command.aggregateId()).isEqualTo(generatedId);
        assertThat(command.eventType()).isEqualTo(EventTypeEnum.URL_CREATED);
        assertThat(command.topic()).isEqualTo(TopicEnum.URL_CREATED);
        assertThat(command.payload()).isInstanceOf(UrlCreatedEvent.class);

        verifyNoMoreInteractions(idGen, mapper, passwordEncoder, repository, outbox);
    }

    @Test
    void shouldCreateUrlSuccessfullyWithoutPassword() {
        CreateUrlDTO dtoWithoutPassword = new CreateUrlDTO(
                dto.originalUrl(),
                dto.title(),
                dto.description(),
                dto.faviconUrl(),
                dto.domain(),
                dto.accessType(),
                null,
                dto.expiresAt()
        );

        UrlModel mappedWithoutPassword = new UrlModel();
        mappedWithoutPassword.setDescription(dto.description());
        mappedWithoutPassword.setFaviconUrl(dto.faviconUrl());
        mappedWithoutPassword.setOriginalUrl(dto.originalUrl());
        mappedWithoutPassword.setTitle(dto.title());
        mappedWithoutPassword.setDomain(dto.domain());
        mappedWithoutPassword.setStatus(UrlStatusEnum.ACTIVE);
        mappedWithoutPassword.setAccessType(dto.accessType());
        mappedWithoutPassword.setCustomAlias(false);

        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dtoWithoutPassword)).thenReturn(mappedWithoutPassword);

        when(repository.insert(any(UrlModel.class))).thenAnswer(invocation -> {
            UrlModel arg = invocation.getArgument(0);
            arg.setId(generatedId);
            arg.setVersion(1L);
            arg.setShortCode(expectedShortCode);
            arg.setCreatedAt(LocalDateTime.now());
            arg.setUpdatedAt(LocalDateTime.now());
            return arg;
        });

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        Result<UrlModel> result = service.execute(dtoWithoutPassword, userId);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getPasswordHash()).isNull();
        assertThat(result.getValue().getAccessType()).isEqualTo(UrlAccessTypeEnum.PUBLIC);

        ArgumentCaptor<UrlModel> urlCaptor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).insert(urlCaptor.capture());

        UrlModel inserted = urlCaptor.getValue();
        assertThat(inserted.getUserId()).isEqualTo(userId);
        assertThat(inserted.getShortCode()).isEqualTo(expectedShortCode);
        assertThat(inserted.getPasswordHash()).isNull();

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldReturn409WhenShortCodeAlreadyExists() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key",
                        new RuntimeException("uk_urls_short_code")
                ));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getMessage())
                .contains("Short code")
                .contains(expectedShortCode);

        verify(idGen).nextId();
        verify(mapper).toModel(dto);
        verify(passwordEncoder).encode(dto.password());
        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldReturn400WhenGenericIntegrityViolationOccurs() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key",
                        new RuntimeException("some_other_constraint")
                ));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).contains("Database integrity error");

        verify(idGen).nextId();
        verify(mapper).toModel(dto);
        verify(passwordEncoder).encode(dto.password());
        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldThrowInternalServerErrorWhenUnexpectedExceptionOccurs() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");
        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new RuntimeException("unexpected"));

        assertThatThrownBy(() -> service.execute(dto, userId))
                .isInstanceOf(InternalServerErrorException.class)
                .hasMessage("unexpected");

        verify(idGen).nextId();
        verify(mapper).toModel(dto);
        verify(passwordEncoder).encode(dto.password());
        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldReturn404WhenUserForeignKeyDoesNotExist() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "fk violation",
                        new RuntimeException("fk_urls_user")
                ));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage()).containsIgnoringCase("user not found");

        verify(idGen).nextId();
        verify(mapper).toModel(dto);
        verify(passwordEncoder).encode(dto.password());
        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldReturn400WhenRequiredFieldIsNull() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "cannot be null",
                        new RuntimeException("Column 'original_url' cannot be null")
                ));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .containsIgnoringCase("Required field 'original_url' is missing");

        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldReturn400WhenDataTooLongOccurs() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "too long",
                        new RuntimeException("Data too long for column 'title'")
                ));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage())
                .containsIgnoringCase("exceeded the allowed size");

        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldReturn400WhenIntegrityMessageIsNull() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        RuntimeException root = mock(RuntimeException.class);
        when(root.getMessage()).thenReturn(null);

        when(repository.insert(any(UrlModel.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "integrity",
                        root
                ));

        Result<UrlModel> result = service.execute(dto, userId);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(400);
        assertThat(result.getMessage()).isEqualTo("Database integrity error");

        verify(repository).insert(any(UrlModel.class));
        verifyNoInteractions(outbox);
    }

    @Test
    void shouldGenerateShortCodeCorrectly() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        service.execute(dto, userId);

        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).insert(captor.capture());

        UrlModel inserted = captor.getValue();
        assertThat(inserted.getShortCode()).isEqualTo(expectedShortCode);
    }

    @Test
    void shouldSetCustomAliasAsFalse() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        service.execute(dto, userId);

        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).insert(captor.capture());

        UrlModel inserted = captor.getValue();
        assertThat(inserted.isCustomAlias()).isFalse();
    }

    @Test
    void shouldMapDtoCorrectly() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        service.execute(dto, userId);

        verify(mapper).toModel(dto);
    }

    @Test
    void shouldSetUserIdCorrectly() {
        when(idGen.nextId()).thenReturn(generatedId);
        when(mapper.toModel(dto)).thenReturn(mappedUrl);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded-password");

        when(repository.insert(any(UrlModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outbox.execute(any(CreateOutboxEventCommand.class)))
                .thenReturn(Result.success(null, 201));

        service.execute(dto, userId);

        ArgumentCaptor<UrlModel> captor = ArgumentCaptor.forClass(UrlModel.class);
        verify(repository).insert(captor.capture());

        UrlModel inserted = captor.getValue();
        assertThat(inserted.getUserId()).isEqualTo(userId);
    }
}