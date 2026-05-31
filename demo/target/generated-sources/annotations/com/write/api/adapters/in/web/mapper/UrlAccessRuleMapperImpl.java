package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlAccessRule.UrlAccessRuleResponseDTO;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-31T18:43:59-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UrlAccessRuleMapperImpl implements UrlAccessRuleMapper {

    @Override
    public UrlAccessRuleResponseDTO toDTO(UrlAccessRuleModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        Long urlId = null;
        UrlAccessRuleTypeEnum type = null;
        String ruleValue = null;
        boolean active = false;
        Long assignedByUserId = null;
        LocalDateTime expiresAt = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        urlId = model.getUrlId();
        type = model.getType();
        ruleValue = model.getRuleValue();
        active = model.isActive();
        assignedByUserId = model.getAssignedByUserId();
        expiresAt = model.getExpiresAt();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        UrlAccessRuleResponseDTO urlAccessRuleResponseDTO = new UrlAccessRuleResponseDTO( id, urlId, type, ruleValue, active, assignedByUserId, expiresAt, createdAt, updatedAt );

        return urlAccessRuleResponseDTO;
    }
}
