package com.read.api.application.usecase.user;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.SaveUserUseCaseImpl;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaveUserUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private SaveUserUseCaseImpl useCase;

    @Test
    void should_save_user() {

        UserModel user = createUser();

        when(repository.save(user))
                .thenReturn(user);

        var result = useCase.execute(user);

        assertTrue(result.isSuccess());
        assertEquals(200, result.getStatusCode());
        assertNotNull(result.getValue());

        assertEquals(user.getId(), result.getValue().getId());
        assertEquals(user.getName(), result.getValue().getName());
        assertEquals(user.getEmail(), result.getValue().getEmail());

        verify(repository, times(1))
                .save(user);
    }
}