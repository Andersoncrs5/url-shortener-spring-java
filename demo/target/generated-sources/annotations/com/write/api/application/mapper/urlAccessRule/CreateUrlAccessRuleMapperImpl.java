package com.write.api.application.mapper.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-02T20:04:24-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class CreateUrlAccessRuleMapperImpl implements CreateUrlAccessRuleMapper {

    @Override
    public UrlAccessRuleModel toDomain(CreateUrlAccessRuleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UrlAccessRuleModel urlAccessRuleModel = new UrlAccessRuleModel();

        urlAccessRuleModel.setRuleValue( dto.ruleValue() );
        urlAccessRuleModel.setUrlId( dto.urlId() );
        urlAccessRuleModel.setType( dto.type() );
        urlAccessRuleModel.setExpiresAt( dto.expiresAt() );

        return urlAccessRuleModel;
    }
}
