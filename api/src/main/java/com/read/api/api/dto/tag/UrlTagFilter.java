package com.read.api.api.dto.tag;

import com.read.api.api.dto.base.BaseFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlTagFilter extends BaseFilter {

    Long userId;
    String name;
    String slug;
    String color;
    String description;
    Long parentId;
    Boolean active;

}
