package com.read.api.api.dto.tag;

import com.read.api.api.dto.base.BaseFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlTagFilter", description = "Dynamic multi-criteria query matrix structure used to find and group system classification labels")
public class UrlTagFilter extends BaseFilter {

    @Schema(description = "Filter records owned specifically by an explicit unique user profile ID", example = "9921")
    Long userId;

    @Schema(description = "Partial text string matching pattern to look up matching display tag names", example = "Marketing")
    String name;

    @Schema(description = "Exact match or text pattern criteria targeting the URL friendly slug string mapping", example = "marketing-q3")
    String slug;

    @Schema(description = "Isolate labels sharing an exact visual hexadecimal identity color hash definition", example = "#FF5733")
    String color;

    @Schema(description = "Substring lookup key targeting historical text definitions inside tag descriptions")
    String description;

    @Schema(description = "Isolate specific children branches by filtering items linked to a target master parent ID context", example = "4412")
    Long parentId;

    @Schema(description = "Isolate tags bound specifically to active or suspended application lifecycle states", example = "true")
    Boolean active;
}