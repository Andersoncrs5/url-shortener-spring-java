package com.read.api.application.usecase.user;

import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.InsertUserUseCaseImpl;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InsertUserUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private InsertUserUseCaseImpl useCase;

    @Test
    void should_insert_user_successfully() {

        UserModel user = createUser();

        when(repository.insert(user))
                .thenReturn(user);

        var result = useCase.execute(user);

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());

        assertEquals(user.getId(), result.getValue().getId());
        assertEquals(user.getName(), result.getValue().getName());
        assertEquals(user.getEmail(), result.getValue().getEmail());

        verify(repository).insert(user);
    }
}