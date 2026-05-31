package com.write.api.application.mapper.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T18:43:59-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UpdateUrlRedirectRuleServiceMapperImpl implements UpdateUrlRedirectRuleServiceMapper {

    @Override
    public void update(UpdateUrlRedirectRuleDTO dto, UrlRedirectRuleModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.countryCode() != null ) {
            model.setCountryCode( dto.countryCode() );
        }
        if ( dto.region() != null ) {
            model.setRegion( dto.region() );
        }
        if ( dto.continent() != null ) {
            model.setContinent( dto.continent() );
        }
        if ( dto.os() != null ) {
            model.setOs( dto.os() );
        }
        if ( dto.browser() != null ) {
            model.setBrowser( dto.browser() );
        }
        if ( dto.matchType() != null ) {
            model.setMatchType( dto.matchType() );
        }
        if ( dto.redirectUrl() != null ) {
            model.setRedirectUrl( dto.redirectUrl() );
        }
        if ( dto.priority() != null ) {
            model.setPriority( dto.priority() );
        }
        if ( dto.active() != null ) {
            model.setActive( dto.active() );
        }
        if ( dto.startAt() != null ) {
            model.setStartAt( dto.startAt() );
        }
        if ( dto.endAt() != null ) {
            model.setEndAt( dto.endAt() );
        }
    }
}
