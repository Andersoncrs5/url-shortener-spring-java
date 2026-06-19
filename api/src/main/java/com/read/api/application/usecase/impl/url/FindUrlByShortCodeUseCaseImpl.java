package com.read.api.application.usecase.impl.url;

import com.read.api.api.dto.url.AccessContextDTO;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.FindUrlByShortCodeUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.FindAllUrlAccessRuleByUrlIdUseCase;
import com.read.api.domain.enums.UrlStatusEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.UrlRedirectRuleRepository;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.result.Result;
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
    public Result<UrlModel> execute(String code, AccessContextDTO dto) {
        String key = "url:" + code;

        var cached = redis.find(key, UrlModel.class);
        if (cached.isPresent()) {
            return Result.success(cached.get());
        }

        UrlModel url = repository.findByShortCode(code).orElse(null);

        if (url == null) {
            return Result.failure(
                    "Url not found",
                    404
            );
        }

        if (!url.getStatus().equals(UrlStatusEnum.ACTIVE)) {
            return Result.failure("Url is " + url.getStatus().name(), 400);
        }

        List<UrlAccessRuleModel> accessRules = findAllUrl.execute(url.getId());
        List<UrlRedirectRuleModel> redirectRules =
                urlRedirectRuleRepository.findActiveRulesByUrlId(url.getId());

        LocalDateTime now = LocalDateTime.now();
        for (UrlAccessRuleModel rule : accessRules) {
            if (!rule.isActive()) continue;
            if (rule.getExpiresAt() != null && rule.getExpiresAt().isBefore(now)) continue;

            switch (rule.getType()) {
                case EXPIRES_AT -> {
                    LocalDateTime expiresAt = LocalDateTime.parse(rule.getRuleValue());

                    if (now.isAfter(expiresAt)) {
                        return Result.failure(
                                "Link expired",
                                403
                        );
                    }
                }

                case MAX_CLICKS -> {
                    long currentClicks = 0L;
                    long maxClicks = Long.parseLong(rule.getRuleValue());

                    if (currentClicks >= maxClicks) {
                        return Result.failure(
                                "Maximum clicks reached",
                                403
                        );
                    }
                }

                case PASSWORD -> {
                    return Result.failure(
                            "Password required",
                            401
                    );
                }

                case COUNTRY_BLOCK -> {

                    if (dto.countryCode().isEmpty()) {
                        return Result.failure(
                                "Country Code is required",
                                400
                        );
                    }

                    boolean blocked = rule.getRuleValue().equalsIgnoreCase(dto.countryCode().get());

                    if (blocked) {
                        return Result.failure(
                                "This country is blocked",
                                403
                        );
                    }
                }

                case IP_BLOCK -> {

                    if (dto.ip().isEmpty()) {
                        return Result.failure(
                                "Ip is required",
                                400
                        );
                    }

                    boolean blocked = rule.getRuleValue().equalsIgnoreCase(dto.ip().get());

                    if (blocked) {
                        return Result.failure(
                                "This IP is blocked",
                                403
                        );
                    }
                }

                case COUNTRY_ALLOW -> {
                    if (dto.countryCode().isEmpty()) {
                        return Result.failure(
                                "Country Code is required",
                                400
                        );
                    }

                    boolean allowed = rule.getRuleValue().equalsIgnoreCase(dto.countryCode().get());

                    if (!allowed) {
                        return Result.failure(
                                "Country not allowed",
                                403
                        );
                    }
                }

                case IP_ALLOW -> {
                    if (dto.ip().isEmpty()) {
                        return Result.failure(
                                "Ip is required",
                                400
                        );
                    }

                    boolean allowed = rule.getRuleValue().equalsIgnoreCase(dto.ip().get());

                    if (!allowed) {
                        return Result.failure(
                                "IP not allowed",
                                403
                        );
                    }
                }

                case REQUIRE_AUTH -> {

                    boolean authenticated = dto.authenticated().orElse(false);

                    if (!authenticated) {
                        return Result.failure(
                                "Authentication required",
                                401
                        );
                    }
                }

                case USER_AGENT_BLOCK -> {

                    if (dto.browser().isEmpty()) {
                        return Result.failure(
                                "Browser is required",
                                400
                        );
                    }

                    boolean blocked =
                            rule.getRuleValue()
                                    .equalsIgnoreCase(
                                            dto.browser().get().name()
                                    );

                    if (blocked) {
                        return Result.failure(
                                "Browser blocked",
                                403
                        );
                    }
                }

                case RATE_LIMIT -> {
                    if (dto.ip().isEmpty()) {
                        return Result.failure(
                                "Ip is required",
                                400
                        );
                    }

                    String rateKey = "rate-limit:" + url.getId() + ":" + dto.ip().get();

                    long current = redis.increment(rateKey);

                    redis.expire(rateKey, Duration.ofMinutes(1));

                    long max = Long.parseLong(rule.getRuleValue());

                    if (current > max) {
                        return Result.failure(
                                "Too many requests",
                                429
                        );
                    }
                }
            }
        }

        UrlRedirectRuleModel selectedRule = null;

        for (UrlRedirectRuleModel rule : redirectRules) {
            if (!rule.isActive()) { continue; }
            if (rule.getStartAt() != null && now.isBefore(rule.getStartAt())) { continue; }
            if (rule.getEndAt() != null && now.isAfter(rule.getEndAt())) { continue; }
            if (!matches(rule, dto)) { continue; }
            if (selectedRule == null || rule.getPriority() > selectedRule.getPriority()) { selectedRule = rule; }
        }

        if (selectedRule != null) {

            UrlModel redirectUrl = new UrlModel();

            redirectUrl.setId(url.getId());
            redirectUrl.setShortCode(url.getShortCode());
            redirectUrl.setOriginalUrl(
                    selectedRule.getRedirectUrl()
            );

            redis.save(
                    key,
                    redirectUrl,
                    Duration.ofMinutes(10)
            );

            return Result.success(redirectUrl);
        }

        redis.save(
                key,
                url,
                Duration.ofMinutes(10)
        );

        return Result.success(url, 301);
    }

    private boolean matches(
            UrlRedirectRuleModel rule,
            AccessContextDTO dto
    ) {

        if (rule.getCountryCode() != null) {

            if (dto.countryCode().isEmpty()) {
                return false;
            }

            if (!rule.getCountryCode().equalsIgnoreCase(dto.countryCode().get())) {
                return false;
            }
        }

        if (rule.getRegion() != null) {

            if (dto.region().isEmpty()) {
                return false;
            }

            if (!rule.getRegion().equalsIgnoreCase(dto.region().get())) {
                return false;
            }
        }

        if (rule.getContinent() != null) {

            if (dto.continent().isEmpty()) {
                return false;
            }

            if (rule.getContinent() != dto.continent().get()) {
                return false;
            }
        }

        if (rule.getOs() != null) {

            if (dto.os().isEmpty()) {
                return false;
            }

            if (rule.getOs() != dto.os().get()) {
                return false;
            }
        }

        if (rule.getBrowser() != null) {

            if (dto.browser().isEmpty()) {
                return false;
            }

            if (rule.getBrowser() != dto.browser().get()) {
                return false;
            }
        }

        return true;
    }
}