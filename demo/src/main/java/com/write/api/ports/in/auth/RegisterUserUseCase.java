package com.write.api.ports.in.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.shared.Result;

public interface RegisterUserUseCase {
    Result<AuthTokenResponseDTO> execute(CreateUserDTO dto);
}
