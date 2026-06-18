package com.read.api.api.dto.tag;

import com.read.api.api.dto.base.BaseDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlTagDTO extends BaseDTO {

    Long userId;
    String name;
    String slug;
    String color;
    String description;
    Long parentId;
    boolean active;

}
