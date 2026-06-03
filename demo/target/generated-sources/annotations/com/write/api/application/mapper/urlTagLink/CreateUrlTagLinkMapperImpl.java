package com.write.api.application.mapper.urlTagLink;

import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-03T09:40:26-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class CreateUrlTagLinkMapperImpl implements CreateUrlTagLinkMapper {

    @Override
    public UrlTagLinkModel toModel(CreateUrlTagLinkDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UrlTagLinkModel urlTagLinkModel = new UrlTagLinkModel();

        urlTagLinkModel.setUrlId( dto.urlId() );
        urlTagLinkModel.setTagId( dto.tagId() );
        urlTagLinkModel.setSortOrder( dto.sortOrder() );
        urlTagLinkModel.setNote( dto.note() );
        urlTagLinkModel.setPrimaryTag( dto.primaryTag() );

        return urlTagLinkModel;
    }
}
