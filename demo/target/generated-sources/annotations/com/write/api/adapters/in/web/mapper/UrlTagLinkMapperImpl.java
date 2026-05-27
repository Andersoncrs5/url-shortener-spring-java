package com.write.api.adapters.in.web.mapper;

import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.core.domain.model.UrlTagLinkModel;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-26T20:56:25-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class UrlTagLinkMapperImpl implements UrlTagLinkMapper {

    @Override
    public UrlTagLinkDTO toDTO(UrlTagLinkModel model) {
        if ( model == null ) {
            return null;
        }

        Long id = null;
        Long urlId = null;
        Long tagId = null;
        Short sortOrder = null;
        String note = null;
        boolean primaryTag = false;
        Long createdBy = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = model.getId();
        urlId = model.getUrlId();
        tagId = model.getTagId();
        sortOrder = model.getSortOrder();
        note = model.getNote();
        primaryTag = model.isPrimaryTag();
        createdBy = model.getCreatedBy();
        createdAt = model.getCreatedAt();
        updatedAt = model.getUpdatedAt();

        UrlTagLinkDTO urlTagLinkDTO = new UrlTagLinkDTO( id, urlId, tagId, sortOrder, note, primaryTag, createdBy, createdAt, updatedAt );

        return urlTagLinkDTO;
    }

    @Override
    public UrlTagLinkModel toModel(UrlTagLinkDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UrlTagLinkModel urlTagLinkModel = new UrlTagLinkModel();

        urlTagLinkModel.setId( dto.id() );
        urlTagLinkModel.setUrlId( dto.urlId() );
        urlTagLinkModel.setTagId( dto.tagId() );
        urlTagLinkModel.setSortOrder( dto.sortOrder() );
        urlTagLinkModel.setNote( dto.note() );
        urlTagLinkModel.setPrimaryTag( dto.primaryTag() );
        urlTagLinkModel.setCreatedBy( dto.createdBy() );
        urlTagLinkModel.setCreatedAt( dto.createdAt() );
        urlTagLinkModel.setUpdatedAt( dto.updatedAt() );

        return urlTagLinkModel;
    }
}
