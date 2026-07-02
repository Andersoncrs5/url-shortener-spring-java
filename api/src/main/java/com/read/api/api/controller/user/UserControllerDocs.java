package com.read.api.api.controller.user;

import com.read.api.api.controller.base.swagger.classes.ResponseBooleanDTO;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.user.UserDTO;
import com.read.api.api.dto.user.UserFilter;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Endpoints for user management, identity checking, and registry lookup")
public interface UserControllerDocs {

    @GetMapping
    @Operation(
            summary = "Find all Users with filtering",
            description = "Retrieves a paginated aggregate of registered users matching the dynamic filter criteria."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paginated array of users returned successfully"),
            @ApiResponse(responseCode = "429", description = "Too many requests",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<Page<?>> getAll(
            @Parameter(description = "Unique key used to guarantee idempotency across analytical queries", required = true, example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234", in = ParameterIn.HEADER)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,

            @ParameterObject @ModelAttribute UserFilter filter,
            @ParameterObject @ModelAttribute UserPageRequestDTO page
    );

    @GetMapping("/{id}")
    @Operation(
            summary = "Find User by ID",
            description = "Retrieves structural registry information and profile details for a specific user identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile context resolved successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "44", description = "User target entity not found",
                    content = @Content(schema = @Schema(implementation = ResponseHTTP.class)))
    })
    ResponseEntity<ResponseHTTP<UserDTO>> findById(
            @Parameter(description = "Unique user numerical system identifier", required = true, example = "9182", in = ParameterIn.PATH)
            @PathVariable Long id,

            @Parameter(description = "Unique key used to guarantee idempotency context window validation", required = true, example = "6f1a4d93-8d3d-4c4f-9f0a-3b8f4c0d1234", in = ParameterIn.HEADER)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    );

    @GetMapping("/email-exists")
    @Operation(
            summary = "Verify Email Registry Uniqueness",
            description = "Checks if an identity email string is already bound to an active registry record inside storage."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email availability criteria evaluated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseBooleanDTO.class)))
    })
    ResponseEntity<ResponseHTTP<Boolean>> emailExists(
            @Parameter(description = "The target semantic email address to verify", required = true, example = "dev@read.api", in = ParameterIn.QUERY)
            @RequestParam String email
    );

    @GetMapping("/name-exists")
    @Operation(
            summary = "Verify Username Registry Uniqueness",
            description = "Checks if a unique user identifier login name is already claimed by another system profile context."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Username target check evaluated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseBooleanDTO.class)))
    })
    ResponseEntity<ResponseHTTP<Boolean>> nameExists(
            @Parameter(description = "The raw user login name to check", required = true, example = "john_doe", in = ParameterIn.QUERY)
            @RequestParam String name
    );
}