package com.read.api.application.usecase.services.user;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.FindByIdUserUseCaseImpl;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindByIdUserUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private FindByIdUserUseCaseImpl useCase;

    @Test
    void should_return_user_when_found() {
        UserModel user = createUser();

        when(repository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Result<UserModel> result = useCase.execute(user.getId());

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(user.getId(), result.getValue().getId());
    }

    @Test
    void should_return_failure_when_user_not_found() {
        Long id = 999L;

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Result<UserModel> result = useCase.execute(id);

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("User not found", result.getMessage());
    }
}