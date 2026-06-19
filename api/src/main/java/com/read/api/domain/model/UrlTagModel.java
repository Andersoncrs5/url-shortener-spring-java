package com.read.api.domain.model;

import com.read.api.domain.model.base.BaseModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
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
