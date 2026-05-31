package com.write.api.application.mapper.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T18:43:59-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UpdateUrlAccessRuleMapperImpl implements UpdateUrlAccessRuleMapper {

    @Override
    public void update(UpdateUrlAccessRuleDTO dto, UrlAccessRuleModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.type() != null ) {
            model.setType( dto.type() );
        }
        if ( dto.ruleValue() != null ) {
            model.setRuleValue( dto.ruleValue() );
        }
        if ( dto.active() != null ) {
            model.setActive( dto.active() );
        }
        if ( dto.expiresAt() != null ) {
            model.setExpiresAt( dto.expiresAt() );
        }
    }
}
