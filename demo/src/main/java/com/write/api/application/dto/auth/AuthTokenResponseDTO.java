package com.write.api.application.dto.auth;

import com.write.api.core.domain.model.UserModel;

import java.util.List;

public record AuthTokenResponseDTO(
        String token,
        String refreshToken,
        UserModel user,
        List<String> roles
) {
}
