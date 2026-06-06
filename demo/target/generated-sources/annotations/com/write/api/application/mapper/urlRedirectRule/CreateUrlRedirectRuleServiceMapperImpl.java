package com.write.api.application.mapper.urlRedirectRule;

import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.core.domain.model.UrlRedirectRuleModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:15-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
)
@Component
public class CreateUrlRedirectRuleServiceMapperImpl implements CreateUrlRedirectRuleServiceMapper {

    @Override
    public UrlRedirectRuleModel toModel(CreateUrlRedirectRuleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UrlRedirectRuleModel urlRedirectRuleModel = new UrlRedirectRuleModel();

        urlRedirectRuleModel.setUrlId( dto.urlId() );
        urlRedirectRuleModel.setCountryCode( dto.countryCode() );
        urlRedirectRuleModel.setRegion( dto.region() );
        urlRedirectRuleModel.setContinent( dto.continent() );
        urlRedirectRuleModel.setOs( dto.os() );
        urlRedirectRuleModel.setBrowser( dto.browser() );
        urlRedirectRuleModel.setMatchType( dto.matchType() );
        urlRedirectRuleModel.setRedirectUrl( dto.redirectUrl() );
        urlRedirectRuleModel.setPriority( dto.priority() );
        if ( dto.active() != null ) {
            urlRedirectRuleModel.setActive( dto.active() );
        }
        urlRedirectRuleModel.setStartAt( dto.startAt() );
        urlRedirectRuleModel.setEndAt( dto.endAt() );

        return urlRedirectRuleModel;
    }
}
