package com.read.api.api.dto.user;

import com.read.api.api.dto.base.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(name = "UserDTO", description = "Detailed structural core profile and authority mapping of a registered identity account")
public class UserDTO extends BaseDTO {

    @Schema(description = "The unique display username handle assigned to this identity profile", example = "john_doe")
    String name;

    @Schema(description = "The primary contact communication and verification email address string", example = "dev@read.api")
    String email;

    @Schema(description = "The active authorization access flag indicating if the user is permitted to interact with the platform gateway", example = "true")
    boolean active;

    @Schema(description = "Collection matrix containing the string keys of security authority roles assigned to this account context", example = "[\"ROLE_ADMIN\", \"ROLE_USER\"]")
    ArrayList<String> roles = new ArrayList<>();

    @Schema(description = "Server clock timestamp tracking the exact moment the client last processed a successful login validation loop", example = "2026-07-02T15:40:12", nullable = true)
    LocalDateTime lastLoginAt;

    @Schema(description = "The administrative suspension or safety quarantine timestamp marker log", example = "2026-06-15T10:22:18", nullable = true)
    LocalDateTime blockedAt;
}