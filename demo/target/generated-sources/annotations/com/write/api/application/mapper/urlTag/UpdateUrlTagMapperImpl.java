package com.write.api.application.mapper.urlTag;

import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.core.domain.model.UrlTagModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-28T14:54:21-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UpdateUrlTagMapperImpl implements UpdateUrlTagMapper {

    @Override
    public void updateModelFromDto(UpdateUrlTagDTO dto, UrlTagModel model) {
        if ( dto == null ) {
            return;
        }

        if ( dto.name() != null ) {
            model.setName( dto.name() );
        }
        if ( dto.slug() != null ) {
            model.setSlug( dto.slug() );
        }
        if ( dto.color() != null ) {
            model.setColor( dto.color() );
        }
        if ( dto.description() != null ) {
            model.setDescription( dto.description() );
        }
        if ( dto.parentId() != null ) {
            model.setParentId( dto.parentId() );
        }
        model.setActive( dto.active() );
    }
}
