package com.read.api.api.dto.role;

import com.read.api.api.dto.base.BaseFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "RoleFilter", description = "Dynamic parameter matrix used to look up and filter active or archived security authorization roles")
public class RoleFilter extends BaseFilter {

    @Schema(description = "Partial text or exact match lookup criterion for the authority key identifier name string", example = "ADMIN")
    String name;

    @Schema(description = "Substring lookup key to locate structural patterns within active role descriptions", example = "administrative capabilities")
    String description;

    @Schema(description = "Isolate roles bound specifically to an explicit active or suspended state status scenario", example = "true")
    Boolean active;
}