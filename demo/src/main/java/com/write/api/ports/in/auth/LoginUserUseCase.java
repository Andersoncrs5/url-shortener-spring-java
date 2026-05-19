package com.write.api.ports.in.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.Result;

public interface LoginUserUseCase {
    Result<AuthTokenResponseDTO> login(LoginUserDTO dto);
}
