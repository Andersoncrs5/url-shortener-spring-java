package com.read.api.api.controller.deadLetterEvent;

import com.read.api.api.controller.base.DefaultApiResponses;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventDTO;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
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

@Tag(name = "Dead Letter Event", description = "Management and auditing of failed integration events")
@DefaultApiResponses
public interface DeadLetterEventControllerDocs {

    @GetMapping("/{id}")
    @Operation(
            summary = "Find Dead Letter Event by ID",
            description = """
                Retrieves the detailed information of a specific failed event stored in the Dead Letter Queue database.
                
                This is helpful for infrastructure auditing and finding underlying reasons for unhandled exceptions.
                
                Requirements:
                - The event ID must be a valid positive identifier.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dead Letter Event found successfully",
                    content = @Content(
                            schema = @Schema(implementation = DeadLetterEventDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dead Letter Event not found with the provided ID",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests - Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))
            )
    })
    ResponseEntity<ResponseHTTP<DeadLetterEventDTO>> findById(
            @Parameter(
                    description = "Unique Dead Letter Event identifier",
                    required = true,
                    example = "918273645",
                    in = ParameterIn.PATH
            )
            @IsId
            @PathVariable Long id
    );

    @GetMapping
    @Operation(
            summary = "Find all Dead Letter Events with filtering",
            description = """
                Retrieves a paginated list of failed events based on dynamic query criteria.
                
                You can filter records by properties like 'eventType', date ranges, or failure keywords.
                
                Results are returned in a paginated format containing meta-data information for UI control.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paginated list retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter parameters provided",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests - Rate limit exceeded",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))
            )
    })
    ResponseEntity<Page<DeadLetterEventDTO>> findAllFilter(
            @ParameterObject @ModelAttribute DeadLetterEventFilter filter,
            @ParameterObject @ModelAttribute DeadLetterEventPageRequestDTO page
    );
}
