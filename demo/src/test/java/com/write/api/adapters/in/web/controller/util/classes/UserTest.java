package com.write.api.adapters.in.web.controller.util.classes;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;

public record UserTest(
        CreateUserDTO dto,
        AuthTokenResponseDTO tokens
) {
}
