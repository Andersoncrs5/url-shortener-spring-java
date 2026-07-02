package com.read.api.api.controller.urlAccessRule;

import com.read.api.api.controller.base.DefaultApiResponses;
import com.read.api.api.controller.base.swagger.classes.ResponseUrlAccessRuleDTO;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
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

@DefaultApiResponses
@Tag(name = "URL Access Rules", description = "Endpoints for querying and validating security and access constraints applied to shortened links")
public interface UrlAccessRuleControllerDocs {

    @GetMapping("/{id}")
    @Operation(
            summary = "Find Access Rule by ID",
            description = "Retrieves structural configuration details of an active access rule applied to a URL link."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access rule configuration found successfully",
                    content = @Content(schema = @Schema(implementation = ResponseUrlAccessRuleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Access rule not found",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<UrlAccessRuleDTO>> findById(
            @Parameter(description = "Unique access rule database identifier", required = true, example = "550e8400", in = ParameterIn.PATH)
            @PathVariable Long id
    );

    @GetMapping
    @Operation(
            summary = "Find all Access Rules with filtering",
            description = "Retrieves a paginated matrix of target configurations mapped to URL links under dynamic query matching criteria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated list retrieved successfully"),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<Page<UrlAccessRuleDTO>> findAllFilter(
            @ParameterObject @ModelAttribute UrlAccessRuleFilter filter,
            @ParameterObject @ModelAttribute UrlAccessRulePageRequestDTO page
    );

    @GetMapping("/exists")
    @Operation(
            summary = "Verify Duplicate Rule Constraints",
            description = """
                Checks if an identical access rule matrix is already deployed or assigned to a target link.
                
                This acts as an optimization predicate check to handle rapid user interaction feedback safely before running expensive mutations.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation executed successfully (returns true if a match exists, false otherwise)",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<Boolean>> exists(
            @Parameter(description = "The database context link owner identifier", required = true, example = "1024", in = ParameterIn.QUERY)
            @RequestParam @IsId Long urlId,

            @Parameter(description = "The target validation behavior constraint to search for", required = true, in = ParameterIn.QUERY)
            @RequestParam UrlAccessRuleTypeEnum type,

            @Parameter(description = "The exact argument payload parameter criteria (e.g., 'US', '192.168.1.1')", required = true, example = "US", in = ParameterIn.QUERY)
            @RequestParam String ruleValue
    );
}