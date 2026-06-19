package com.read.api.infrastructure.persistence.entity;

import com.read.api.infrastructure.persistence.entity.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "tags")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UrlTagEntity extends BaseEntity {

    Long userId;
    String name;
    String slug;
    String color;
    String description;
    Long parentId;
    boolean active;

}
