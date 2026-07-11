package com.notify.notify.services.user;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.globals.services.redis.RedisCrudService;
import com.notify.notify.modules.user.dto.UserCdcEvent;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.mapper.UserMapper;
import com.notify.notify.modules.user.services.base.DeleteUserByIdService;
import com.notify.notify.modules.user.services.base.InsertUserService;
import com.notify.notify.modules.user.services.base.SyncUserService;
import com.notify.notify.modules.user.services.provider.SyncUserCdcServiceImpl;
import com.notify.notify.services.base.BaseServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("SyncUserCdcServiceImpl Unit Tests")
public class SyncUserCdcServiceImplTest extends BaseServiceTest {

    @Mock
    SyncUserService syncUser;

    @Mock
    InsertUserService insertUser;

    @Mock
    DeleteUserByIdService deleteUserById;

    @Mock
    RedisCrudService redis;

    @Mock
    UserMapper mapper;

    @InjectMocks
    SyncUserCdcServiceImpl syncUserCdcService;

    @Mock
    TiCdcEvent<UserCdcEvent> mockEvent;

    @Mock
    UserCdcEvent mockUserCdcEvent;

    UserEntity userEntity;
    String eventId;

    @BeforeEach
    void setUp() {
        when(mockEvent.table()).thenReturn("users");
        when(mockEvent.ts()).thenReturn(123456789L);
        when(mockEvent.es()).thenReturn(1L);
        eventId = "users:123456789:1";

        userEntity = new UserEntity();
        userEntity.setEmail("test@notify.com");
    }

    @Nested
    @DisplayName("Idempotency & Duplication Tests")
    class IdempotencyTests {

        @Test
        @DisplayName("Should skip processing when event already exists in Redis")
        void shouldReturnSuccessAndSkipWhenEventIsDuplicated() {
            // Arrange
            when(redis.exists(eventId)).thenReturn(true);

            // Act
            Result<UserEntity> result = syncUserCdcService.execute(mockEvent);

            // Assert
            assertTrue(result.isSuccess());
            verify(redis, times(1)).exists(eventId);
            verifyNoInteractions(syncUser, deleteUserById, mapper);
            verify(redis, never()).save(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Insert Event Tests")
    class InsertEventTests {

        @Test
        @DisplayName("Should successfully process insert event and save to Redis")
        void shouldProcessInsertSuccessfully() {
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(true);
            when(mockEvent.firstData()).thenReturn(mockUserCdcEvent);
            when(mapper.toEntity(mockUserCdcEvent)).thenReturn(userEntity);
            when(insertUser.execute(userEntity)).thenReturn(Result.success(userEntity));

            Result<UserEntity> result = syncUserCdcService.execute(mockEvent);

            assertTrue(result.isSuccess());
            assertEquals(userEntity, result.getValue());
            verify(redis).save(eq(eventId), eq("processed"), any(Duration.class));
        }

        @Test
        @DisplayName("Should return failure and not save to Redis when insert logic fails")
        void shouldReturnFailureWhenInsertFails() {
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(true);
            when(mockEvent.firstData()).thenReturn(mockUserCdcEvent);
            when(mapper.toEntity(mockUserCdcEvent)).thenReturn(userEntity);

            Result<UserEntity> failedResult = Result.failure("Error microservice connection", HttpStatus.INTERNAL_SERVER_ERROR);
            when(insertUser.execute(userEntity)).thenReturn(failedResult);

            Result<UserEntity> result = syncUserCdcService.execute(mockEvent);

            assertTrue(result.isFailure());
            verify(redis, never()).save(anyString(), anyString(), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("Update Event Tests")
    class UpdateEventTests {

        @Test
        @DisplayName("Should successfully process update event and save to Redis")
        void shouldProcessUpdateSuccessfully() {
            // Arrange
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(true);
            when(mockEvent.firstData()).thenReturn(mockUserCdcEvent);
            when(mapper.toEntity(mockUserCdcEvent)).thenReturn(userEntity);
            when(syncUser.execute(userEntity)).thenReturn(Result.success(userEntity));

            // Act
            Result<UserEntity> result = syncUserCdcService.execute(mockEvent);

            // Assert
            assertTrue(result.isSuccess());
            verify(redis).save(eq(eventId), eq("processed"), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("Delete Event Tests")
    class DeleteEventTests {

        @Test
        @DisplayName("Should successfully process delete event and save to Redis")
        void shouldProcessDeleteSuccessfully() {
            // Arrange
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(false);
            when(mockEvent.isDelete()).thenReturn(true);

            when(mockUserCdcEvent.id()).thenReturn(999L);
            when(mockEvent.old()).thenReturn(List.of(mockUserCdcEvent));
            when(deleteUserById.execute(999L)).thenReturn(Result.success());

            // Act
            Result<UserEntity> result = syncUserCdcService.execute(mockEvent);

            // Assert
            assertTrue(result.isSuccess());
            verify(redis).save(eq(eventId), eq("processed"), any(Duration.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when old data is null or empty on delete")
        void shouldThrowExceptionWhenOldDataIsEmptyOnDelete() {
            // Arrange
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(false);
            when(mockEvent.isDelete()).thenReturn(true);
            when(mockEvent.old()).thenReturn(Collections.emptyList());

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                syncUserCdcService.execute(mockEvent);
            });

            assertEquals("Delete event without old data", exception.getMessage());
            verifyNoInteractions(deleteUserById);
        }
    }

    @Nested
    @DisplayName("Exception & Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should throw BusinessException when event type is unknown")
        void shouldThrowExceptionWhenEventTypeIsUnsupported() {
            // Arrange
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(false);
            when(mockEvent.isDelete()).thenReturn(false); // Tipo desconhecido

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                syncUserCdcService.execute(mockEvent);
            });

            assertEquals("Unsupported CDC event", exception.getMessage());
        }

        @Test
        @DisplayName("Should wrap any unexpected exception into a BusinessException")
        void shouldWrapUnexpectedExceptionsInBusinessException() {
            // Arrange
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenThrow(new RuntimeException("Database down"));

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                syncUserCdcService.execute(mockEvent);
            });

            assertEquals("Database down", exception.getMessage());
        }
    }
}