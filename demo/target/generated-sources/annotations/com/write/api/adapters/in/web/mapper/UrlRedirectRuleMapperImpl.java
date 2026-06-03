package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-03T09:40:26-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UrlRedirectRuleMapperImpl implements UrlRedirectRuleMapper {

    @Override
    public UrlRedirectRuleDTO toDTO(UrlRedirectRuleModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        Long urlId = null;
        String countryCode = null;
        String region = null;
        ContinentEnum continent = null;
        OperatingSystemEnum os = null;
        BrowserEnum browser = null;
        MatchTypeEnum matchType = null;
        String redirectUrl = null;
        String ruleHash = null;
        Integer priority = null;
        boolean active = false;
        LocalDateTime startAt = null;
        LocalDateTime endAt = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        urlId = model.getUrlId();
        countryCode = model.getCountryCode();
        region = model.getRegion();
        continent = model.getContinent();
        os = model.getOs();
        browser = model.getBrowser();
        matchType = model.getMatchType();
        redirectUrl = model.getRedirectUrl();
        ruleHash = model.getRuleHash();
        priority = model.getPriority();
        active = model.isActive();
        startAt = model.getStartAt();
        endAt = model.getEndAt();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        UrlRedirectRuleDTO urlRedirectRuleDTO = new UrlRedirectRuleDTO( id, urlId, countryCode, region, continent, os, browser, matchType, redirectUrl, ruleHash, priority, active, startAt, endAt, createdAt, updatedAt );

        return urlRedirectRuleDTO;
    }
}
