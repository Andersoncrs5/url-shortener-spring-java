package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlTagModel extends BaseModel {

    Long userId;
    String name;
    String slug;
    String color;
    String description;
    Long parentId;
    boolean active;

}