package com.read.api.api.dto.user;

import com.read.api.api.controller.user.UserOrderBy;
import com.read.api.api.dto.base.BaseFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "UserFilter", description = "Dynamic criteria query parameter structure used to look up, audit, and slice the identity registry layer")
public class UserFilter extends BaseFilter {

    @Schema(description = "Partial text matching string pattern to lookup accounts by username handles", example = "john")
    String name;

    @Schema(description = "Partial or exact match lookup criterion targeting identity communication email strings", example = "dev@")
    String email;

    @Schema(description = "Isolate records based on current active deployment authorization or suspension states", example = "true")
    Boolean active;

    @Schema(description = "The dynamic structural data sorting execution rule assigned to rank the result list matrix", example = "NAME_ASC")
    UserOrderBy order;
}