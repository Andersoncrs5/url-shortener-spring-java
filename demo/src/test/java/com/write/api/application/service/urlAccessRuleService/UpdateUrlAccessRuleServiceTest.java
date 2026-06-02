package com.write.api.application.service.urlAccessRuleService;

import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.application.mapper.urlAccessRule.UpdateUrlAccessRuleMapper;
import com.write.api.application.service.urlAccessRule.UpdateUrlAccessRuleService;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUrlAccessRuleServiceTest {

    @Mock
    private IUrlAccessRuleRepository repository;

    @Mock
    private UpdateUrlAccessRuleMapper mapper;

    @InjectMocks
    private UpdateUrlAccessRuleService service;

    @Test
    void shouldUpdateRuleSuccessfully() {
        Long id = 1L;

        UpdateUrlAccessRuleDTO dto = mock(UpdateUrlAccessRuleDTO.class);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setId(id);

        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        when(repository.save(rule))
                .thenReturn(rule);

        Result<UrlAccessRuleModel> result = service.execute(dto, id);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isEqualTo(rule);

        verify(repository).findById(id);
        verify(mapper).update(dto, rule);
        verify(repository).save(rule);
    }

    @Test
    void shouldReturnNotFoundWhenRuleDoesNotExist() {
        Long id = 1L;

        UpdateUrlAccessRuleDTO dto = mock(UpdateUrlAccessRuleDTO.class);

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        Result<UrlAccessRuleModel> result = service.execute(dto, id);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getMessage())
                .isEqualTo("Url Access Rule not found");

        verify(repository).findById(id);
        verifyNoInteractions(mapper);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldCallMapperBeforeSaving() {
        Long id = 1L;

        UpdateUrlAccessRuleDTO dto = mock(UpdateUrlAccessRuleDTO.class);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setId(id);

        when(repository.findById(id))
                .thenReturn(Optional.of(rule));

        when(repository.save(rule))
                .thenReturn(rule);

        service.execute(dto, id);

        verify(mapper, times(1))
                .update(dto, rule);

        verify(repository, times(1))
                .save(rule);
    }

    @Test
    void shouldReturnUpdatedEntity() {
        Long id = 1L;

        UpdateUrlAccessRuleDTO dto = mock(UpdateUrlAccessRuleDTO.class);

        UrlAccessRuleModel updated = new UrlAccessRuleModel();
        updated.setId(id);
        updated.setUrlId(10L);
        updated.setType(UrlAccessRuleTypeEnum.PASSWORD);
        updated.setRuleValue("123456");
        updated.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(repository.findById(id))
                .thenReturn(Optional.of(new UrlAccessRuleModel()));

        when(repository.save(any()))
                .thenReturn(updated);

        Result<UrlAccessRuleModel> result = service.execute(dto, id);

        assertThat(result.getValue()).isNotNull();
        assertThat(result.getValue().getId()).isEqualTo(id);
        assertThat(result.getValue().getType())
                .isEqualTo(UrlAccessRuleTypeEnum.PASSWORD);
    }
}