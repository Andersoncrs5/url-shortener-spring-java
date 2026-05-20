package com.write.api.ports.in.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.shared.Result;

public interface RefreshTokenUseCase {
    Result<AuthTokenResponseDTO> execute(String refreshToken);
}
