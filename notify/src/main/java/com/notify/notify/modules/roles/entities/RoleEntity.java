package com.notify.notify.modules.roles.entities;

import com.notify.notify.utils.base.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity extends BaseEntity {

    private String name;

    private String description;

    private Boolean active;
}