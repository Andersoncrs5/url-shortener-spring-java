package com.notify.notify.services.role;

import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.exceptions.BusinessException;
import com.notify.notify.globals.services.redis.RedisCrudService;
import com.notify.notify.modules.roles.dto.RoleCdcEvent;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.mapper.RoleMapper;
import com.notify.notify.modules.roles.services.base.DeleteRoleByIdService;
import com.notify.notify.modules.roles.services.base.SyncRoleService;
import com.notify.notify.modules.roles.services.provider.SyncRoleCdcServiceImpl;
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

@DisplayName("SyncRoleCdcServiceImpl Unit Tests")
public class SyncRoleCdcServiceImplTest extends BaseServiceTest {

    @Mock
    RedisCrudService redis;

    @Mock
    SyncRoleService sync;

    @Mock
    DeleteRoleByIdService delete;

    @Mock
    RoleMapper mapper;

    @InjectMocks
    SyncRoleCdcServiceImpl syncRoleCdcService;

    @Mock
    TiCdcEvent<RoleCdcEvent> mockEvent;

    @Mock
    RoleCdcEvent mockRoleCdcEvent;

    RoleEntity roleEntity;
    String eventId;

    @BeforeEach
    void setUp() {
        when(mockEvent.table()).thenReturn("roles");
        when(mockEvent.ts()).thenReturn(987654321L);
        when(mockEvent.es()).thenReturn(2L);
        eventId = "roles:987654321:2";

        roleEntity = new RoleEntity();
        roleEntity.setName("ROLE_MANAGER");
    }

    @Nested
    @DisplayName("Idempotency Tests")
    class IdempotencyTests {

        @Test
        @DisplayName("Should ignore duplicated CDC event if key exists in Redis")
        void shouldSkipProcessingWhenEventIsDuplicated() {

            when(redis.exists(eventId)).thenReturn(true);


            Result<RoleEntity> result = syncRoleCdcService.execute(mockEvent);


            assertTrue(result.isSuccess());
            verify(redis, times(1)).exists(eventId);
            verifyNoInteractions(sync, delete, mapper);
            verify(redis, never()).save(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Insert Event Tests")
    class InsertEventTests {

        @Test
        @DisplayName("Should successfully sync insert CDC event and save key to cache")
        void shouldProcessInsertSuccessfully() {

            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(true);
            when(mockEvent.firstData()).thenReturn(mockRoleCdcEvent);
            when(mapper.toEntity(mockRoleCdcEvent)).thenReturn(roleEntity);
            when(sync.execute(roleEntity)).thenReturn(Result.success(roleEntity));


            Result<RoleEntity> result = syncRoleCdcService.execute(mockEvent);


            assertTrue(result.isSuccess());
            assertEquals(roleEntity, result.getValue());
            verify(redis).save(eq(eventId), eq("processed"), any(Duration.class));
        }

        @Test
        @DisplayName("Should return failure and skip redis cache write when insert execution fails")
        void shouldReturnFailureWhenInsertFails() {

            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(true);
            when(mockEvent.firstData()).thenReturn(mockRoleCdcEvent);
            when(mapper.toEntity(mockRoleCdcEvent)).thenReturn(roleEntity);

            Result<RoleEntity> failureResult = Result.failure("Conflict detected", HttpStatus.INTERNAL_SERVER_ERROR);
            when(sync.execute(roleEntity)).thenReturn(failureResult);


            Result<RoleEntity> result = syncRoleCdcService.execute(mockEvent);


            assertTrue(result.isFailure());
            verify(redis, never()).save(anyString(), anyString(), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("Update Event Tests")
    class UpdateEventTests {

        @Test
        @DisplayName("Should successfully sync update CDC event")
        void shouldProcessUpdateSuccessfully() {

            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(true);
            when(mockEvent.firstData()).thenReturn(mockRoleCdcEvent);
            when(mapper.toEntity(mockRoleCdcEvent)).thenReturn(roleEntity);
            when(sync.execute(roleEntity)).thenReturn(Result.success(roleEntity));


            Result<RoleEntity> result = syncRoleCdcService.execute(mockEvent);


            assertTrue(result.isSuccess());
            verify(redis).save(eq(eventId), eq("processed"), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("Delete Event Tests")
    class DeleteEventTests {

        @Test
        @DisplayName("Should successfully process delete event and invoke delete service")
        void shouldProcessDeleteSuccessfully() {

            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(false);
            when(mockEvent.isDelete()).thenReturn(true);

            when(mockRoleCdcEvent.id()).thenReturn(55L);
            when(mockEvent.old()).thenReturn(List.of(mockRoleCdcEvent));
            when(delete.execute(55L)).thenReturn(Result.success());


            Result<RoleEntity> result = syncRoleCdcService.execute(mockEvent);


            assertTrue(result.isSuccess());
            verify(redis).save(eq(eventId), eq("processed"), any(Duration.class));
        }

        @Test
        @DisplayName("Should throw BusinessException when delete payload has no old list data")
        void shouldThrowExceptionWhenOldDataIsEmptyOnDelete() {

            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(false);
            when(mockEvent.isDelete()).thenReturn(true);
            when(mockEvent.old()).thenReturn(Collections.emptyList());


            BusinessException exception = assertThrows(BusinessException.class, () -> {
                syncRoleCdcService.execute(mockEvent);
            });

            assertEquals("Delete event without old data", exception.getMessage());
            verifyNoInteractions(delete);
        }
    }

    @Nested
    @DisplayName("Edge Cases & Exception Scenarios")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should throw BusinessException when action type is unsupported")
        void shouldThrowExceptionWhenActionIsUnsupported() {

            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenReturn(false);
            when(mockEvent.isUpdate()).thenReturn(false);
            when(mockEvent.isDelete()).thenReturn(false);

            BusinessException exception = assertThrows(BusinessException.class, () -> {
                syncRoleCdcService.execute(mockEvent);
            });

            assertEquals("Unsupported CDC event", exception.getMessage());
        }

        @Test
        @DisplayName("Should wrap unexpected infrastructure errors inside a BusinessException")
        void shouldWrapUnexpectedExceptions() {
            when(redis.exists(eventId)).thenReturn(false);
            when(mockEvent.isInsert()).thenThrow(new RuntimeException("Redis connection timeout"));

            BusinessException exception = assertThrows(BusinessException.class, () -> {
                syncRoleCdcService.execute(mockEvent);
            });

            assertEquals("Redis connection timeout", exception.getMessage());
        }
    }
}