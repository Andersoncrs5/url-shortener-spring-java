package com.write.api.application.dto.auth;

import com.write.api.core.domain.model.UserModel;

import java.util.Set;

public record AuthTokenResponseDTO(
        String token,
        String refreshToken,
        UserModel user,
        Set<String> roles
) {
}
