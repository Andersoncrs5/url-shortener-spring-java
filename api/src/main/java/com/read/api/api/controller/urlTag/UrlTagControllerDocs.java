package com.read.api.api.controller.urlTag;

import com.read.api.api.controller.base.DefaultApiResponses;
import com.read.api.api.controller.base.JwtProtected;
import com.read.api.api.controller.base.swagger.classes.ResponseBooleanDTO;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.tag.UrlTagDTO;
import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.utils.validation.isId.IsId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "URL Tags", description = "Endpoints for querying, organizing, and verifying semantic categorization tags applied to links")
@JwtProtected
@DefaultApiResponses
public interface UrlTagControllerDocs {

    @GetMapping
    @Operation(
            summary = "Find all URL Tags with filtering",
            description = "Retrieves a paginated aggregate of classification tags based on filter properties such as names or substrings."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated tag matrix returned successfully"),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<Page<UrlTagDTO>> findAllFilter(
            @ParameterObject @ModelAttribute UrlTagFilter filter,
            @ParameterObject @ModelAttribute UrlTagPageRequestDTO page
    );

    @GetMapping("/{id}")
    @Operation(
            summary = "Find URL Tag by ID",
            description = "Retrieves metadata definitions for an isolated semantic URL classification component."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag metadata resolved successfully",
                    content = @Content(schema = @Schema(implementation = UrlTagDTO.class))),
            @ApiResponse(responseCode = "404", description = "Target identifier resource not found",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<UrlTagDTO>> findById(
            @Parameter(description = "Unique URL tag system identifier", required = true, example = "4312", in = ParameterIn.PATH)
            @PathVariable @IsId Long id
    );

    @GetMapping("/name-exists")
    @Operation(
            summary = "Verify Tag Name Availability",
            description = "Checks if a structural text key is already claimed as a tag name entity within the system storage layer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification map evaluated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseBooleanDTO.class)))
    })
    ResponseEntity<ResponseHTTP<Boolean>> nameExists(
            @Parameter(description = "The literal string name to be verified", required = true, example = "Marketing 2026", in = ParameterIn.QUERY)
            @RequestParam String name
    );

    @GetMapping("/slug-exists")
    @Operation(
            summary = "Verify Tag Slug Availability",
            description = "Checks if a URL-friendly slug pattern variant is already registered inside the repository schema context."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Slug uniqueness verification evaluated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseBooleanDTO.class)))
    })
    ResponseEntity<ResponseHTTP<Boolean>> slugExists(
            @Parameter(description = "The target semantic slug token variant", required = true, example = "marketing-2026", in = ParameterIn.QUERY)
            @RequestParam String slug
    );
}