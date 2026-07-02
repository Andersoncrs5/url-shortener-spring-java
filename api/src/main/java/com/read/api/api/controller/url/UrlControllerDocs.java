package com.read.api.api.controller.url;

import com.read.api.api.controller.base.swagger.classes.ResponseHttpUrl;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.url.UrlDTO;
import com.read.api.api.dto.url.UrlFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "URL Management", description = "Endpoints for retrieving information and resolving shortened URLs")
public interface UrlControllerDocs {

    @GetMapping("/{id}")
    @Operation(
            summary = "Find URL by ID",
            description = "Retrieves metadata and target configurations for a specific shortened URL record."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL found successfully",
                    content = @Content(schema = @Schema(implementation = ResponseHttpUrl.class))),
            @ApiResponse(responseCode = "404", description = "URL resource not found",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<UrlDTO>> findById(
            @Parameter(description = "Unique URL numerical database identifier", required = true, example = "1024", in = ParameterIn.PATH)
            @PathVariable Long id
    );

    @GetMapping
    @Operation(
            summary = "Find all URLs with filtering",
            description = "Retrieves a paginated collection of shortened URLs based on dynamic evaluation parameters."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated matrix returned successfully"),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<Page<UrlDTO>> findAllFilter(
            @ParameterObject @ModelAttribute UrlFilter filter,
            @ParameterObject @ModelAttribute UrlPageRequestDTO page
    );

    @GetMapping("/r/{shortCode}")
    @Operation(
            summary = "Resolve and Redirect Short Code",
            description = """
                Processes the analytical context (User-Agent, IP, location metrics) and verifies compliance rules to resolve a short code.
                
                Security Mechanics:
                - If the target URL is protected by password validation, the 'X-Url-Password' header must be accompanied.
                - Rules like Country blocking, Expire policies, and Max Clicks are verified during this execution context.
                
                HTTP Status Behavior:
                - Generates dynamic responses based on rule evaluation. If used strictly as an API fetch, returns the destination schema.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Short code resolved successfully. Content ready for redirection.",
                    content = @Content(schema = @Schema(implementation = ResponseHttpUrl.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Missing or invalid 'X-Url-Password' for guarded URLs",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - Access denied due to active Geoblocking or rule constraints",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "404", description = "Short code not found or expired",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests on lookup gateway",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<UrlDTO>> redirectShortCode(
            @Parameter(description = "The specific token/slug representing the short link", required = true, example = "b7xK9p", in = ParameterIn.PATH)
            @PathVariable String shortCode,

            @Parameter(description = "Client browser and client hardware fingerprint information string", in = ParameterIn.HEADER)
            @RequestHeader(value = "User-Agent", required = false) String userAgent,

            @Parameter(description = "Optional structural validation token used for encrypted private links", in = ParameterIn.HEADER, example = "securepwd123")
            @RequestHeader(value = "X-Url-Password", required = false) String password,

            @Parameter(hidden = true)
            HttpServletRequest request
    );
}