package com.read.api.application.usecase.services.url;

import com.read.api.api.dto.url.AccessContextDTO;
import com.read.api.application.usecase.base.BaseUseCaseTest;
import com.read.api.application.usecase.impl.url.FindUrlByShortCodeUseCaseImpl;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllUrlAccessRuleByUrlIdUseCase;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FindUrlByShortCodeUseCaseImplTest extends BaseUseCaseTest {

    @Mock
    private UrlRepository repository;

    @Mock
    private FindAllUrlAccessRuleByUrlIdUseCase findAllUrl;

    @Mock
    private UrlRedirectRuleRepository urlRedirectRuleRepository;

    @InjectMocks
    private FindUrlByShortCodeUseCaseImpl useCase;

    @Test
    void should_block_expired_link() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setType(UrlAccessRuleTypeEnum.EXPIRES_AT);
        rule.setActive(true);
        rule.setRuleValue(LocalDateTime.now().minusDays(1).toString());

        when(redis.find(anyString(), eq(UrlModel.class))).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(findAllUrl.execute(url.getId())).thenReturn(List.of(rule));
        when(urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId())).thenReturn(List.of());

        Result<UrlModel> result = useCase.execute("abc", emptyContext());

        assertTrue(result.isFailure());
        assertEquals(403, result.getStatusCode());
        assertEquals("Link expired", result.getMessage());
        verify(repository).save(url);
    }

    @Test
    void should_block_country() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setType(UrlAccessRuleTypeEnum.COUNTRY_BLOCK);
        rule.setRuleValue("BR");
        rule.setActive(true);

        when(redis.find(anyString(), eq(UrlModel.class))).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(findAllUrl.execute(url.getId())).thenReturn(List.of(rule));
        when(urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId())).thenReturn(List.of());

        AccessContextDTO dto = new AccessContextDTO(
                Optional.empty(),
                Optional.of("BR"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        Result<UrlModel> result = useCase.execute("abc", dto);

        assertTrue(result.isFailure());
        assertEquals(403, result.getStatusCode());
        assertEquals("This country is blocked", result.getMessage());
        verify(repository).save(url);
    }

    @Test
    void should_return_url_from_cache_when_present() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        when(redis.find("url:abc", UrlModel.class)).thenReturn(Optional.of(url));

        Result<UrlModel> result = useCase.execute("abc", emptyContext());

        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(url.getId(), result.getValue().getId());

        verify(redis).find("url:abc", UrlModel.class);
        verifyNoInteractions(repository);
        verify(redis, never()).save(anyString(), any(), any());
    }

    @Test
    void should_require_authentication() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setType(UrlAccessRuleTypeEnum.REQUIRE_AUTH);
        rule.setActive(true);

        when(redis.find(anyString(), eq(UrlModel.class))).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(findAllUrl.execute(url.getId())).thenReturn(List.of(rule));
        when(urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId())).thenReturn(List.of());

        Result<UrlModel> result = useCase.execute("abc", emptyContext());

        assertTrue(result.isFailure());
        assertEquals(401, result.getStatusCode());
        assertEquals("Authentication required", result.getMessage());
        verify(repository).save(url);
    }

    @Test
    void should_rate_limit_request() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        UrlAccessRuleModel rule = new UrlAccessRuleModel();
        rule.setType(UrlAccessRuleTypeEnum.RATE_LIMIT);
        rule.setRuleValue("10");
        rule.setActive(true);

        when(redis.find(anyString(), eq(UrlModel.class))).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(findAllUrl.execute(url.getId())).thenReturn(List.of(rule));
        when(urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId())).thenReturn(List.of());
        when(redis.increment(anyString())).thenReturn(11L);

        AccessContextDTO dto = new AccessContextDTO(
                Optional.of("127.0.0.1"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        Result<UrlModel> result = useCase.execute("abc", dto);

        assertTrue(result.isFailure());
        assertEquals(429, result.getStatusCode());
        assertEquals("Too many requests", result.getMessage());
        verify(redis).expire(anyString(), eq(Duration.ofMinutes(1)));
        verify(repository).save(url);
    }

    @Test
    void should_redirect_when_matching_rule_exists() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        UrlRedirectRuleModel rule = new UrlRedirectRuleModel();
        rule.setActive(true);
        rule.setPriority(100);
        rule.setCountryCode("BR");
        rule.setRedirectUrl("https://br.example.com");

        when(redis.find(anyString(), eq(UrlModel.class))).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(findAllUrl.execute(url.getId())).thenReturn(List.of());
        when(urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId())).thenReturn(List.of(rule));

        AccessContextDTO dto = new AccessContextDTO(
                Optional.empty(),
                Optional.of("BR"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        Result<UrlModel> result = useCase.execute("abc", dto);

        assertTrue(result.isSuccess());
        assertEquals("https://br.example.com", result.getValue().getOriginalUrl());
        verify(repository).save(url);
        verify(redis, never()).save(anyString(), any(), any());
    }

    @Test
    void should_return_url_from_repository_and_save_in_cache_when_not_cached() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.ACTIVE);

        when(redis.find("url:abc", UrlModel.class)).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));
        when(findAllUrl.execute(url.getId())).thenReturn(List.of());
        when(urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId())).thenReturn(List.of());

        Result<UrlModel> result = useCase.execute("abc", emptyContext());

        assertTrue(result.isSuccess());
        assertEquals(301, result.getStatusCode());

        verify(redis).find("url:abc", UrlModel.class);
        verify(repository).findByShortCode("abc");
        verify(repository).save(url);
        verify(redis).save(eq("url:abc"), eq(url), eq(Duration.ofMinutes(10)));
    }

    @Test
    void should_return_failure_when_url_not_found() {
        when(redis.find("url:abc", UrlModel.class)).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.empty());

        Result<UrlModel> result = useCase.execute("abc", emptyContext());

        assertFalse(result.isSuccess());
        assertEquals(404, result.getStatusCode());
        assertEquals("Url not found", result.getMessage());

        verify(redis).find("url:abc", UrlModel.class);
        verify(repository).findByShortCode("abc");
        verify(redis, never()).save(anyString(), any(), any());
    }

    @Test
    void should_return_failure_when_url_is_not_active() {
        UrlModel url = createUrl();
        url.setStatus(UrlStatusEnum.DISABLED); // Configura estado inativo para cair na validação

        when(redis.find("url:abc", UrlModel.class)).thenReturn(Optional.empty());
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(url));

        Result<UrlModel> result = useCase.execute("abc", emptyContext());

        assertTrue(result.isFailure());
        assertEquals(400, result.getStatusCode());
        assertEquals("Url is " + url.getStatus().name(), result.getMessage());
    }

    private AccessContextDTO emptyContext() {
        return new AccessContextDTO(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }
}