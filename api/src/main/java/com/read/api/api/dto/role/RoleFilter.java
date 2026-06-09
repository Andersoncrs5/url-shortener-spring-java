package com.read.api.api.dto.role;

import com.read.api.api.dto.base.BaseFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleFilter extends BaseFilter {
    String name;
    String description;
    Boolean active;
}
