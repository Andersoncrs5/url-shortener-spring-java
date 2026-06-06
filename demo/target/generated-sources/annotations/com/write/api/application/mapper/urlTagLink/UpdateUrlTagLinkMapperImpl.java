package com.write.api.application.mapper.urlTagLink;

import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:14-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
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
