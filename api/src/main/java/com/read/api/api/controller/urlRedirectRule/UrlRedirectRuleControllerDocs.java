package com.read.api.api.controller.urlRedirectRule;

import com.read.api.api.controller.base.DefaultApiResponses;
import com.read.api.api.controller.base.swagger.classes.ResponseUrlRedirectRuleDTO;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
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

@DefaultApiResponses
@Tag(name = "URL Redirect Rules", description = "Endpoints for managing and inspecting conditional routing, split-testing, and dynamic destination targets")
public interface UrlRedirectRuleControllerDocs {

    @GetMapping
    @Operation(
            summary = "Find all Redirect Rules with filtering",
            description = "Retrieves a paginated collection of destination rerouting rules applied to links under dynamic execution criteria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated evaluation matrix returned successfully"),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<Page<UrlRedirectRuleDTO>> findAllFilter(
            @ParameterObject @ModelAttribute UrlRedirectRuleFilter filter,
            @ParameterObject @ModelAttribute UrlRedirectRulePageRequestDTO page
    );

    @GetMapping("/{id}")
    @Operation(
            summary = "Find Redirect Rule by ID",
            description = "Retrieves structural parameters, conditional expressions, and targets mapped to an active redirect rule identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Redirect rule metadata found successfully",
                    content = @Content(
                            schema = @Schema(implementation = ResponseUrlRedirectRuleDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Redirect rule context not found",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<UrlRedirectRuleDTO>> findById(
            @Parameter(description = "Unique dynamic rule numerical identifier", required = true, example = "77203", in = ParameterIn.PATH)
            @PathVariable @IsId Long id
    );
}