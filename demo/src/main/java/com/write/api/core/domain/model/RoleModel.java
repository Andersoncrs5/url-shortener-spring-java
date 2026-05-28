package com.write.api.core.domain.model;

import com.write.api.core.domain.model.shared.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleModel extends BaseModel {
    private String name;
    private String description;
    private boolean active;
}
