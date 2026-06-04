package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlTagLinkModel extends BaseModel {

    Long urlId;
    Long tagId;

    Short sortOrder;

    String note;

    boolean primaryTag;

    Long createdBy;

}