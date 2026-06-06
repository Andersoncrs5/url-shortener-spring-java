package com.write.api.application.mapper.urlTag;

import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.shared.mapper.config.EnumMapper;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T10:42:15-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (GraalVM Community)"
)
@Component
public class CreateUrlTagMapperImpl implements CreateUrlTagMapper {

    @Override
    public UrlTagModel toModel(CreateUrlTagDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UrlTagModel urlTagModel = new UrlTagModel();

        urlTagModel.setName( dto.name() );
        urlTagModel.setSlug( dto.slug() );
        urlTagModel.setColor( dto.color() );
        urlTagModel.setDescription( dto.description() );
        urlTagModel.setParentId( dto.parentId() );
        urlTagModel.setActive( dto.active() );

        return urlTagModel;
    }
}
