package com.read.api.api.dto.tag;

import com.read.api.api.dto.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UrlTagDTO", description = "Detailed structural metadata representation of a classification category tag applied to links")
public class UrlTagDTO extends BaseDTO {

    @Schema(description = "The database primary identifier of the user account who owns this categorization entity", example = "9921")
    Long userId;

    @Schema(description = "The customer-facing display text name used for this organization tag", example = "Marketing Q3")
    String name;

    @Schema(description = "The unique URL-friendly text variant identifier token matching this tag structure", example = "marketing-q3")
    String slug;

    @Schema(description = "Hexadecimal color token code used to distinguish this tag component visually in user interfaces", example = "#FF5733")
    String color;

    @Schema(description = "A short explanatory summary describing the purpose or domain mapping of this label", example = "Campaign tracking targets for third quarter acquisitions")
    String description;

    @Schema(description = "The optional parent database tag identifier used to construct nested category trees or sub-tags", example = "4412", nullable = true)
    Long parentId;

    @Schema(description = "The status flag indicating if this label flag can currently be linked or mapped to operational URLs", example = "true")
    boolean active;
}