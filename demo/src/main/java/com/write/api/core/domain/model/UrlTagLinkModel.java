package com.write.api.core.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UrlTagLinkModel {

    private Long id;

    private Long urlId;
    private Long tagId;

    private Short sortOrder;

    private String note;

    private boolean primaryTag;

    private Long createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}