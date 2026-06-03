package com.write.api.application.mapper.urlTagLink;

import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-02T20:04:24-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UpdateUrlTagLinkMapperImpl implements UpdateUrlTagLinkMapper {

    @Override
    public void update(UpdateUrlTagLinkDTO dto, UrlTagLinkModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.sortOrder() != null ) {
            model.setSortOrder( dto.sortOrder() );
        }
        if ( dto.note() != null ) {
            model.setNote( dto.note() );
        }
        if ( dto.primaryTag() != null ) {
            model.setPrimaryTag( dto.primaryTag() );
        }
    }
}
