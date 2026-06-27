package com.read.api.application.usecase.services.deadLetterEvent;

import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.deadLetterEvent.FindAllDeadLetterEventUseCaseImpl;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindAllDeadLetterEventUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private DeadLetterEventRepository repository;

    @InjectMocks
    private FindAllDeadLetterEventUseCaseImpl useCase;

    @Test
    void should_find_all_dead_letter_events_with_pagination_and_filter() {
        DeadLetterEventFilter filter = new DeadLetterEventFilter();
        Pageable pageable = PageRequest.of(0, 10);

        DeadLetterEventModel event = new DeadLetterEventModel();
        event.setId(generator.nextId());
        event.setSourceTopic("user-topic");

        Page<DeadLetterEventModel> expectedPage = new PageImpl<>(List.of(event), pageable, 1);

        when(repository.findAll(filter, pageable))
                .thenReturn(expectedPage);

        Page<DeadLetterEventModel> result = useCase.execute(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(event.getId(), result.getContent().get(0).getId());

        verify(repository).findAll(filter, pageable);
    }
}