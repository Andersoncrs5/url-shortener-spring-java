package com.read.api.api.dto.user;

import com.read.api.api.dto.base.BaseFilter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilter extends BaseFilter {
    String name;
    String email;
    Boolean active;
}
