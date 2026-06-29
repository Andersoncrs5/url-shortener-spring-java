package com.read.api.application.usecase.impl.url;

import com.read.api.api.dto.url.AccessContextDTO;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.FindUrlByShortCodeUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllUrlAccessRuleByUrlIdUseCase;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FindUrlByShortCodeUseCaseImpl implements FindUrlByShortCodeUseCase {

    UrlRepository repository;
    FindAllUrlAccessRuleByUrlIdUseCase findAllUrl;
    UrlRedirectRuleRepository urlRedirectRuleRepository;
    RedisCrudService redis;

    @Override
    @Retry(name = "read")
    @ObservedMetric("url.find.code")
    public Result<UrlModel> execute(String code, AccessContextDTO dto) {
        String key = "url:" + code;

        var cached = redis.find(key, UrlModel.class);
        if (cached.isPresent()) {
            return Result.success(cached.get());
        }

        UrlModel url = repository.findByShortCode(code).orElse(null);

        if (url == null) {
            return Result.failure("Url not found", 404);
        }

        if (!url.getStatus().equals(UrlStatusEnum.ACTIVE)) {
            return Result.failure("Url is " + url.getStatus().name(), 400);
        }

        List<UrlAccessRuleModel> accessRules = findAllUrl.execute(url.getId());
        List<UrlRedirectRuleModel> redirectRules = urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId());

        LocalDateTime now = LocalDateTime.now();
        for (UrlAccessRuleModel rule : accessRules) {
            if (!rule.isActive()) continue;
            if (rule.getExpiresAt() != null && rule.getExpiresAt().isBefore(now)) continue;

            switch (rule.getType()) {
                case EXPIRES_AT -> {
                    LocalDateTime expiresAt = LocalDateTime.parse(rule.getRuleValue());
                    if (now.isAfter(expiresAt)) {
                        return failAndLogMetric(url, rule.getType(), "Link expired", 403);
                    }
                }

                case MAX_CLICKS -> {
                    long currentClicks = url.getMetric().getRedirects();
                    long maxClicks = Long.parseLong(rule.getRuleValue());

                    if (currentClicks >= maxClicks) {
                        return failAndLogMetric(url, rule.getType(), "Maximum clicks reached", 403);
                    }
                }

                case PASSWORD -> {
                    return failAndLogMetric(url, rule.getType(), "Password required", 401);
                }

                case COUNTRY_BLOCK -> {
                    if (dto.countryCode().isEmpty()) {
                        return failAndLogMetric(url, rule.getType(), "Country Code is required", 400);
                    }
                    if (rule.getRuleValue().equalsIgnoreCase(dto.countryCode().get())) {
                        return failAndLogMetric(url, rule.getType(), "This country is blocked", 403);
                    }
                }

                case IP_BLOCK -> {
                    if (dto.ip().isEmpty()) {
                        return failAndLogMetric(url, rule.getType(), "Ip is required", 400);
                    }
                    if (rule.getRuleValue().equalsIgnoreCase(dto.ip().get())) {
                        return failAndLogMetric(url, rule.getType(), "This IP is blocked", 403);
                    }
                }

                case COUNTRY_ALLOW -> {
                    if (dto.countryCode().isEmpty()) {
                        return Result.failure("Country Code is required", 400);
                    }
                    if (!rule.getRuleValue().equalsIgnoreCase(dto.countryCode().get())) {
                        return failAndLogMetric(url, rule.getType(), "Country not allowed", 403);
                    }
                }

                case IP_ALLOW -> {
                    if (dto.ip().isEmpty()) {
                        return failAndLogMetric(url, rule.getType(), "Ip is required", 400);
                    }
                    if (!rule.getRuleValue().equalsIgnoreCase(dto.ip().get())) {
                        return failAndLogMetric(url, rule.getType(), "IP not allowed", 403);
                    }
                }

                case REQUIRE_AUTH -> {
                    boolean authenticated = dto.authenticated().orElse(false);
                    if (!authenticated) {
                        return failAndLogMetric(url, rule.getType(), "Authentication required", 401);
                    }
                }

                case USER_AGENT_BLOCK -> {
                    if (dto.browser().isEmpty()) {
                        return Result.failure("Browser is required", 400);
                    }
                    boolean blocked = rule.getRuleValue().equalsIgnoreCase(dto.browser().get().name());
                    if (blocked) {
                        return failAndLogMetric(url, rule.getType(), "Browser blocked", 403);
                    }
                }

                case RATE_LIMIT -> {
                    if (dto.ip().isEmpty()) {
                        return Result.failure("Ip is required", 400);
                    }
                    String rateKey = "rate-limit:" + url.getId() + ":" + dto.ip().get();
                    long current = redis.increment(rateKey);
                    redis.expire(rateKey, Duration.ofMinutes(1));

                    long max = Long.parseLong(rule.getRuleValue());
                    if (current > max) {
                        return failAndLogMetric(url, rule.getType(), "Too many requests", 429);
                    }
                }
            }
        }

        UrlRedirectRuleModel selectedRule = null;
        for (UrlRedirectRuleModel rule : redirectRules) {
            if (!rule.isActive()) continue;
            if (rule.getStartAt() != null && now.isBefore(rule.getStartAt())) continue;
            if (rule.getEndAt() != null && now.isAfter(rule.getEndAt())) continue;
            if (!matches(rule, dto)) continue;
            if (selectedRule == null || rule.getPriority() > selectedRule.getPriority()) {
                selectedRule = rule;
            }
        }

        url.getMetric().incrementRedirects();

        if (selectedRule != null) {
            UrlModel redirectUrl = new UrlModel();
            redirectUrl.setId(url.getId());
            redirectUrl.setShortCode(url.getShortCode());
            redirectUrl.setOriginalUrl(selectedRule.getRedirectUrl());

            repository.save(url);

            return Result.success(redirectUrl);
        }

        dto.continent().ifPresent(c -> url.getMetric().incrementContinent(c));
        dto.browser().ifPresent(b -> url.getMetric().incrementBrowser(b));
        dto.os().ifPresent(o -> url.getMetric().incrementOperatingSystem(o));

        repository.save(url);
        redis.save(key, url, Duration.ofMinutes(10));

        return Result.success(url, 301);
    }

    private Result<UrlModel> failAndLogMetric(UrlModel url, UrlAccessRuleTypeEnum type, String message, int statusCode) {
        url.getMetric().incrementBlocked(type);
        repository.save(url);
        return Result.failure(message, statusCode);
    }

    private boolean matches(UrlRedirectRuleModel rule, AccessContextDTO dto) {
        if (rule.getCountryCode() != null && (dto.countryCode().isEmpty() || !rule.getCountryCode().equalsIgnoreCase(dto.countryCode().get()))) return false;
        if (rule.getRegion() != null && (dto.region().isEmpty() || !rule.getRegion().equalsIgnoreCase(dto.region().get()))) return false;
        if (rule.getContinent() != null && (dto.continent().isEmpty() || rule.getContinent() != dto.continent().get())) return false;
        if (rule.getOs() != null && (dto.os().isEmpty() || rule.getOs() != dto.os().get())) return false;
        if (rule.getBrowser() != null && (dto.browser().isEmpty() || rule.getBrowser() != dto.browser().get())) return false;
        return true;
    }
}