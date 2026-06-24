package com.read.api.application.usecase.services.user;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.user.FindAllUserUseCaseImpl;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindAllUserUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private FindAllUserUseCaseImpl useCase;

    @Test
    void should_return_all_users() {

        var user = createUser();

        var page = new PageImpl<>(
                List.of(user, user, user),
                PageRequest.of(0, 10),
                3
        );

        when(
                repository.findAll(
                        any(UserFilter.class),
                        any(PageRequest.class)
                )
        ).thenReturn(page);

        var result = useCase.execute(
                new UserFilter(),
                PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());

        verify(repository, times(1))
                .findAll(any(UserFilter.class), any(PageRequest.class));
    }

}